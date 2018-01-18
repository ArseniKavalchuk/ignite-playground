package ru.synesis.kipod.facematch.ignite;

import static java.lang.Math.min;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.synesis.kipod.event.FaceKipodEvent;
import ru.synesis.kipod.utils.ArrayHelper;
import ru.synesis.kipod.utils.DescriptorHelper;
import ru.synesis.kipod.utils.MathHelper;

public class RecognitionServiceImpl implements Service, RecognitionService {

    private static final long serialVersionUID = 8166510322950097012L;

    private static final int PARALLELIZM = 8; // cannot rely on Runtime.availableProcessors in Kubernetes

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @IgniteInstanceResource
    private Ignite ignite;

    private ExecutorService executorSrv;

    private final String cacheName;

    public RecognitionServiceImpl(String cacheName) {
        this.cacheName = cacheName;
    }

    public Collection<Map.Entry<FaceKipodEvent, Float>> findSimilarParallel(List<float[]> batch, float minSimilarity, QueryFilter filter) throws CacheException {

        if (logger.isDebugEnabled()) {
            logger.debug("Matching descriptors: {} with min similarity {}", batch, minSimilarity);
        }

        float threshold = MathHelper.similarityToDistance(minSimilarity);

        List<CompletableFuture<Collection<Map.Entry<FaceKipodEvent, Float>>>> futures = new ArrayList<>();

        int[] partitions = ignite.affinity(cacheName).primaryPartitions(ignite.cluster().localNode());

        if (logger.isDebugEnabled()) {
            logger.debug("Partitions: {} ", partitions.length);
        }

        int[][] chunks = ArrayHelper.chunkIntArray(partitions, PARALLELIZM);

        final IgniteCache<String, FaceKipodEvent> cache = ignite.cache(cacheName);
        if (logger.isDebugEnabled()) {
            logger.debug("Using cache: {}", cache);
        }

        for (int[] subPart : chunks) {
            final CompletableFuture<Collection<Map.Entry<FaceKipodEvent, Float>>> f = CompletableFuture.supplyAsync(() -> {
                Collection<Map.Entry<FaceKipodEvent, Float>> partialResult = new HashSet<>();
                for (int partition : subPart) {
                    cache.query(new ScanQuery<>((IgniteBiPredicate<String, FaceKipodEvent>) (key, faceDetectedEvent) -> {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Matching : {}", faceDetectedEvent);
                        }
                        if (!filter.pass(faceDetectedEvent)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Event {} has not passed the filter", key);
                            }
                            return false;
                        }
                        float minDst = Float.MAX_VALUE;
                        for (float[] dscr : faceDetectedEvent.descriptors) {
                            for (float[] b : batch) {
                                float dst = DescriptorHelper.distance(dscr, b);
                                minDst = min(minDst, dst);
                            }
                        }
                        if (minDst <= threshold) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Event {} matches with min distance {}", key, minDst);
                            }
                            partialResult.add(new AbstractMap.SimpleImmutableEntry<>(faceDetectedEvent,
                                    MathHelper.distanceToSimilarity(minDst)));
                            return true;
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Event {} doesn't match with min distance {}", key, minDst);
                        }
                        return false;
                    }).setLocal(true).setPartition(partition)).getAll();
                }
                return partialResult;
            }, executorSrv).exceptionally(e -> {
                logger.error("Error:", e);
                return Collections.emptySet();
            });

            futures.add(f);
        }

        Collection<Map.Entry<FaceKipodEvent, Float>> result = new HashSet<>();
        for (CompletableFuture<Collection<Map.Entry<FaceKipodEvent, Float>>> f : futures) {
            try {
                result.addAll(f.get(1, TimeUnit.MINUTES));
            } catch (Exception e) {
                ignite.log().error("Error: ", e);
            }
        }
        return result;
    }

    @Override
    public Collection<Map.Entry<FaceKipodEvent, Float>> findSimilar(List<float[]> batch, float minSimilarity,   QueryFilter filter) throws CacheException {
        float threshold = MathHelper.similarityToDistance(minSimilarity);
        List<Map.Entry<FaceKipodEvent, Float>> result = new ArrayList<>();
        IgniteCache<String, FaceKipodEvent> cache = ignite.cache(cacheName);
        cache.query(new ScanQuery<>((IgniteBiPredicate<String, FaceKipodEvent>) (key, faceDetectedEvent) -> {
            if (!filter.pass(faceDetectedEvent)) {
                return false;
            }
            float minDst = Float.MAX_VALUE;
            for (float[] dscr : faceDetectedEvent.descriptors) {
                for (float[] b : batch) {
                    float dst = DescriptorHelper.distance(dscr, b);
                    minDst = min(minDst, dst);
                }
            }

            if (minDst <= threshold) {
                result.add(new AbstractMap.SimpleImmutableEntry<>(faceDetectedEvent, MathHelper.distanceToSimilarity(minDst)));
                return true;
            }

            return false;
        }).setLocal(true), Cache.Entry::getKey).getAll();

        return result;
    }

    @Override
    public void cancel(ServiceContext serviceContext) {
        ignite.log().info("RecognitionService canceled");
    }

    @Override
    public void init(ServiceContext serviceContext) throws Exception {
        executorSrv = Executors.newFixedThreadPool(PARALLELIZM, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        ignite.log().info("RecognitionService initialized");
    }

    @Override
    public void execute(ServiceContext serviceContext) throws Exception {
        ignite.log().info("RecognitionService started");
    }
}
