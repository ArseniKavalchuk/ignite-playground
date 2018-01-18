package ru.synesis.kipod.test;

import static ru.synesis.kipod.event.EventConstants.KIPOD_EVENTS;
import static ru.synesis.kipod.service.EventGeneratorUtil.generateEvents;

import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ru.synesis.kipod.event.KipodEvent;
import ru.synesis.kipod.service.ExpiredEventsCleaner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"default", "local"})
@SpringBootTest(classes = {
    IgniteTestConfiguration.class
})
public class IgniteQueryTest {

    public static class Config {
        
        @Bean
        public ExpiredEventsCleaner eventsCleaner() {
            return new ExpiredEventsCleaner();
        }
        
    }
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private volatile boolean eventsReady = false;
    
    @Autowired
    @Qualifier("igniteClient")
    private Ignite ignite;
    
    @Before
    public void setUp() {
        synchronized (this) {
            if (!eventsReady) {
                Map<String, KipodEvent> events = generateEvents(100_000);
                ignite.cache(KIPOD_EVENTS).putAll(events);
                eventsReady = true;
            }
        }
    }
    
    @Test
    public void testQuery() throws Exception {
        IgniteCache<String, KipodEvent> cache = ignite.cache(KIPOD_EVENTS);
        SqlFieldsQuery q = new SqlFieldsQuery("SELECT _key, _val FROM KipodEvent WHERE face_first_name = ? AND face_last_name = ?");
        q.setArgs("edward", "bitch");
        
        try (FieldsQueryCursor<List<?>> cursor = cache.query(q)) {
            log.debug("{}", cursor.getAll());
        }
    }
    
}
