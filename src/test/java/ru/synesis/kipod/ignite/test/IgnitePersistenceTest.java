package ru.synesis.kipod.ignite.test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSpringBean;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import ru.synesis.kipod.ignite.NumberEvent;
import ru.synesis.kipod.ignite.NumberGen;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IgnitePersistenceTest {

    private static final String[] TOPICS = {
        "Topic 1", "Topic 2", "Topic 3", "Topic 4"
    };

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Random rnd = new Random();

    @Autowired
    @Qualifier("igniteClient")
    private Ignite ignite;
    
    @Test
    public void testPersistence() throws Exception {
        
        CacheConfiguration<Long, NumberEvent> conf = new CacheConfiguration<>();
        conf
            .setName("persistence_test")
            .setAtomicityMode(CacheAtomicityMode.ATOMIC)
            .setBackups(0)
            .setStatisticsEnabled(false)
            .setManagementEnabled(false)
            .setCacheMode(CacheMode.PARTITIONED)
            .setCopyOnRead(false)
            .setMemoryPolicyName("512MB_Region_Policy")
            .setRebalanceMode(CacheRebalanceMode.NONE)
            .setIndexedTypes(String.class, NumberEvent.class)
            .setStoreByValue(false);
        
        ignite.active(true);
        
        IgniteCache<String, NumberEvent> cache = ignite
            .getOrCreateCache(conf)
            .withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.HOURS, 1)))
            .withKeepBinary();
        Map<String, NumberEvent> batch = new ConcurrentSkipListMap<>();
//        SqlFieldsQuery q1 = new SqlFieldsQuery("select count(1) from \"persistence_test\".NumberEvent");
//        SqlFieldsQuery q2 = new SqlFieldsQuery("select count(1) from \"persistence_test\".NumberEvent where topic = 'Topic 4'");
        for (long i = 0; i < 1_000_000L; i++) {
            long now = System.currentTimeMillis();
            batch.put(String.valueOf(i), NumberEvent.create(i, TOPICS[rnd.nextInt(4)], now, now, NumberGen.generateNumber()));
            if ((i + 1) % 10_000 == 0) {
                long st = System.currentTimeMillis();
                cache.putAll(batch);
                long en = System.currentTimeMillis();
                log.info("Batch iserted in {} ms", en - st);
                batch = new ConcurrentSkipListMap<>();
                long offHeap    = cache.sizeLong(CachePeekMode.OFFHEAP);
                long onHeap     = cache.sizeLong(CachePeekMode.ONHEAP);
                long prim       = cache.sizeLong(CachePeekMode.PRIMARY);
                log.info("Cache stats prim: {} onHeap: {} offHeap: {}", prim, onHeap, offHeap);
            }
/*            if ((i + 1) % 1_000_000 == 0) {
                long q1_st = System.currentTimeMillis();
                long count1 = (long) cache.query(q1).getAll().get(0).get(0);
                long q1_en = System.currentTimeMillis();
                log.info("Count query {} in {} ms", count1, (q1_en - q1_st));
                long q2_st = System.currentTimeMillis();
                long count2 = (long) cache.query(q2).getAll().get(0).get(0);
                long q2_en = System.currentTimeMillis();
                log.info("Select query {} in {} ms", count2, (q2_en - q2_st));
            }
*/        }

    }
    
    @Configuration
    @ImportResource({
        "classpath:ignite-persistent-context.xml"
    })
    @EnableConfigurationProperties
    public static class Config {
        
        @Bean("igniteServer1")
        public Ignite igniteServer1(@Qualifier("igniteCfg1") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
            IgniteSpringBean bean = new IgniteSpringBean();
            bean.setConfiguration(igniteCfg);
            bean.setApplicationContext(applicationContext);
            return bean;
        }
        
        @Bean("igniteServer2")
        public Ignite igniteServer2(@Qualifier("igniteCfg2") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
            IgniteSpringBean bean = new IgniteSpringBean();
            bean.setConfiguration(igniteCfg);
            bean.setApplicationContext(applicationContext);
            return bean;
        }

        @Bean("igniteClient")
        public Ignite ignite(@Qualifier("igniteCfg3") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
            IgniteSpringBean bean = new IgniteSpringBean();
            bean.setConfiguration(igniteCfg);
            bean.setApplicationContext(applicationContext);
            return bean;
        }
        
    }
    
}
