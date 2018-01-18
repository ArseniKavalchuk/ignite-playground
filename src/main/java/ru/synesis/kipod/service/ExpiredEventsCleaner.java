package ru.synesis.kipod.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.synesis.kipod.event.KipodEvent;

@Component
public class ExpiredEventsCleaner {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("igniteClient")
    private Ignite ignite;
    
    private IgniteCache<String, KipodEvent> cache;
    
    @EventListener
    public void onCacheEvent(CacheReadyEvent event) {
        log.debug("Connecting to cache");
        cache = ignite.cache(event.getCacheName());
    }
    
    @Scheduled(fixedRate = 15000)
    public void clean() {
        if (cache == null) {
            log.debug("Cache is not ready yet. Skip cleaning.");
            return;
        }
        SqlFieldsQuery q = new SqlFieldsQuery("DELETE FROM KipodEvent WHERE expiration <= ?");
        q.setArgs(System.currentTimeMillis());
        cache.query(q);
    }
}

