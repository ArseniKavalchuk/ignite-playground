package ru.synesis.kipod.ignite.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSpringBean;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IgniteIndexTest {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final String SELECT_EVENT = "SELECT id, topic, start_time, end_time FROM KipodEvent ORDER BY start_time LIMIT 5";
    private static final String INSERT_EVENT = "INSERT INTO KipodEvent(id, topic, start_time, end_time) VALUES(?, ?, ?, ?)";
    private static final String DELETE_ALL_EVENT = "DELETE FROM KipodEvent";
    
    private static final String[] TOPICS = {
        "Topic 1", "Topic 2", "Topic 3", "Topic 4"
    };
    
    private Random rnd = new Random();
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private Ignite ignite;
    
    public static class ArrayObject {
        @QuerySqlField(index = true)
        public long id;
        
        @QuerySqlField(index = true)
        public String[] tags;
    }
    
    @Test
    public void binaryObjectTest() throws Exception {
        
        CacheConfiguration<String, ArrayObject> conf = new CacheConfiguration<>();
        conf.setName("array_event");
        conf.setIndexedTypes(String.class, ArrayObject.class);
        
        IgniteCache<String, ArrayObject> binCache = ignite.getOrCreateCache(conf);
        Map<String, ArrayObject> objects = new TreeMap<>();
        for (int i = 1; i < 100; i++) {
            ArrayObject obj = new ArrayObject();
            obj.id = (long) i;
            obj.tags = new String[] {"tag1", "tag2", "tag3"};
            objects.put(String.valueOf(i), obj);
        }
        binCache.putAll(objects);
        SqlFieldsQuery sql = new SqlFieldsQuery("SELECT id, tags FROM \"array_event\".ArrayObject WHERE ARRAY_CONTAINS(tags, ?)");
        sql.setArgs("tag3");
        binCache.query(sql).forEach(row -> {
            log.info(">>>> {}", row);
        });
        
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    //@Test
    public void sqlCacheLoadTest() throws Exception {
        ignite.active(true);
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource("create.sql"));
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM KipodEvent", Long.class);
        log.info("Starts with {} items in cache", count);
        if (count == null || count.equals(0L)) {
            ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource("indexes.sql"));
        }
        Long maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM KipodEvent", Long.class);
        long i = maxId == null ? 1 : maxId + 1;
        long dataLoadStart = System.currentTimeMillis();
        //loadData(i);
        long dataLoadStop = System.currentTimeMillis();
        log.info("Insert total exec: {}ms", dataLoadStop - dataLoadStart);
        long startSelect = System.currentTimeMillis();
        log.info("{}", jdbcTemplate.queryForList(SELECT_EVENT));
        long endSelect = System.currentTimeMillis();
        log.info("Select exec: {}ms", endSelect - startSelect);
    }
    
    @Configuration
    @ImportResource({
        "classpath:ignite-mem-context.xml"
    })
    @EnableConfigurationProperties
    public static class Config {
        
        @Bean
        public Ignite ignite(@Qualifier("igniteCfg1") IgniteConfiguration igniteCfg, ApplicationContext applicationContext) {
            IgniteSpringBean bean = new IgniteSpringBean();
            bean.setConfiguration(igniteCfg);
            bean.setApplicationContext(applicationContext);
            return bean;
        }
        
        @Bean
        @ConfigurationProperties("ignite.datasource")
        public DataSource igniteDataSource() {
            return new DriverManagerDataSource();
        }
        
        @Bean
        public JdbcTemplate igniteJdbcTemplate(DataSource igniteDataSource) {
            return new JdbcTemplate(igniteDataSource);
        }
        
    }
}
