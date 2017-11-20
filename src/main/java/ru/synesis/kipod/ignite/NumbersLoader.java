package ru.synesis.kipod.ignite;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicy;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class NumbersLoader implements ApplicationRunner {
    
    private static final long TOTAL = 100_000_000;
    private static final long BATCH = 100_000;

    private static final String INSERT_EVENT = "INSERT INTO event_num_test(id, topic, start_time, end_time, numval) VALUES(?, ?, ?, ?, ?)";
    private static final String[] TOPICS = {
        "Topic 1", "Topic 2", "Topic 3", "Topic 4"
    };

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Random rnd = new Random();
    
    @Autowired
    private Ignite ignite;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ignite.active(true);
        loadViaApi();
    }
    
    void loadViaApi() {
        log.info("Begin to load data");
        long dataLoadStart = System.currentTimeMillis();
        CacheConfiguration<Long, NumberEvent> conf = new CacheConfiguration<>();
        conf
            .setName("number_event")
            .setAtomicityMode(CacheAtomicityMode.ATOMIC)
            .setBackups(0)
            .setStatisticsEnabled(false)
            .setManagementEnabled(false)
            .setCacheMode(CacheMode.PARTITIONED)
            .setCopyOnRead(false)
            .setOnheapCacheEnabled(true)
            .setMemoryPolicyName("Custom_Region_Eviction")
            .setStoreKeepBinary(true)
            .setEvictionPolicy(new FifoEvictionPolicy<>(100_000_000))
            .setRebalanceMode(CacheRebalanceMode.NONE)
            .setIndexedTypes(Long.class, NumberEvent.class)
            .setStoreByValue(false);
        IgniteCache<Long, NumberEvent> cache = ignite.getOrCreateCache(conf);
        Map<Long, NumberEvent> batch = new HashMap<>();
        for (long i = 1; i <= TOTAL; i++) {
            long now = System.currentTimeMillis();
            if (i % BATCH == 0) {
                log.info("Put a batch");
                cache.putAll(batch);
                batch = new HashMap<>();
            }
            batch.put(i, NumberEvent.create(i, TOPICS[rnd.nextInt(4)], now, now, NumberGen.generateNumber()));
        }
        if (batch.size() > 0) cache.putAll(batch);
        batch = null;
        long dataLoadStop = System.currentTimeMillis();
        log.info("Data load took: {} ms", dataLoadStop - dataLoadStart);
    }
    
    void loadViaSQL() throws Exception {
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource("create_numbers.sql"));
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM event_num_test", Long.class);
        log.info("Starting db with {} events", count);
        log.info("Begin to load data");
        long dataLoadStart = System.currentTimeMillis();
        loadData(1L);
        long dataLoadStop = System.currentTimeMillis();
        log.info("Data load took: {} ms", dataLoadStop - dataLoadStart);
    }

    private void loadData(long startId) {
        List<List<Object>> batch = new LinkedList<>();
        long end = startId + 1_000_000;
        for (; startId <= end; startId++) {
            long now = System.currentTimeMillis();
            if (startId % 100 == 0) {
                long st = System.currentTimeMillis();
                insertData(batch);
                long en = System.currentTimeMillis();
                log.info("Insert {} items in {} ms", batch.size(), en - st);
                batch.clear();
            }
            List<Object> row = new LinkedList<>();
            row.add(startId);
            row.add(TOPICS[rnd.nextInt(4)]);
            row.add(now);
            row.add(now);
            row.add(NumberGen.generateNumber());
            batch.add(row);
        }
    }
    
    private void insertData(final List<List<Object>> data) {
        jdbcTemplate.batchUpdate(INSERT_EVENT, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                List<Object> row = data.get(i);
                ps.setLong(1, (Long) row.get(0));
                ps.setString(2, (String) row.get(1));
                ps.setLong(3, (Long) row.get(2));
                ps.setLong(4, (Long) row.get(3));
                ps.setString(5, (String) row.get(4));
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
            
        });
    }
    
}
