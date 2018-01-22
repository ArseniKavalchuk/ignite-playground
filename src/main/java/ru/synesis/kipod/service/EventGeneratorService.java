package ru.synesis.kipod.service;

import static ru.synesis.kipod.service.EventGeneratorUtil.generateEvents;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.synesis.kipod.event.KipodEvent;


@Component
@DependsOn("igniteClient")
public class EventGeneratorService implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final static int EVENT_BATCH_SIZE = 1000;
    
    @Autowired
    @Qualifier("igniteClient")
    private Ignite ignite;
    
    @Autowired
    @Qualifier("kipodEventCacheConfig")
    private CacheConfiguration<String, KipodEvent> cacheConfig;
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    private IgniteCache<String, KipodEvent> cache;
    
    private AtomicBoolean cacheReady = new AtomicBoolean(false);
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (ignite.active() == false) {
            if (log.isInfoEnabled()) log.info("Ignite is not active. Activating...");
            ignite.active(true);
        }
        cacheConfig.setAffinity(new CustomAffinityFunction());
        cache = ignite.getOrCreateCache(cacheConfig);
        cacheReady.set(true);
        publisher.publishEvent(new CacheReadyEvent(cacheConfig.getName()));
    }
    
    @Scheduled(fixedRate = 1500)
    public void eventGenerator() {
        if (!cacheReady.get()) {
            if (log.isDebugEnabled()) log.debug("Cache is not ready yet. Skip event generation");
            return;
        }
        Map<String, KipodEvent> events = generateEvents(EVENT_BATCH_SIZE);
        if (log.isDebugEnabled()) {
            log.debug("Put {} events", events.size());
        }
        cache.putAll(events);
    }
    
    @Scheduled(fixedRate = 5000)
    public void logStaticstics() {
        if (!cacheReady.get()) {
            log.debug("Cache is not ready yet. No stats.");
            return;
        }
    }
 
}
