package ru.synesis.kipod.facematch.ignite;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.cache.CacheException;

import ru.synesis.kipod.event.FaceKipodEvent;

public interface RecognitionService {
    Collection<Map.Entry<FaceKipodEvent, Float>> findSimilar(List<float[]> batch, float minSimilarity, QueryFilter filter)
            throws CacheException;

    Collection<Map.Entry<FaceKipodEvent, Float>> findSimilarParallel(List<float[]> batch, float minSimilarity,
            QueryFilter filter) throws CacheException;
}
