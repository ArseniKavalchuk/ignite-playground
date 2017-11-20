package ru.synesis.kipod.ignite.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteBinary;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ru.synesis.kipod.ignite.IgniteApplication.Config.class)
public class IgniteLoadTest {
    
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
    
    //@Test
    public void binaryObjectTest() throws Exception {
        IgniteCache<Long, BinaryObject> binCache = ignite.getOrCreateCache("kx_events");
        IgniteBinary igniteBinary = ignite.binary();
        BinaryObjectBuilder builder = igniteBinary.builder("java.lang.Object");
        Map<Long, BinaryObject> objects = new HashMap<>();
        for (int i = 1; i < 100; i++) {
            builder.setField("id", (long) i, Long.class);
            builder.setField("topic", TOPICS[rnd.nextInt(4)], String.class);
            builder.setField("start_time", System.currentTimeMillis(), Long.class);
            builder.setField("end_time", System.currentTimeMillis(), Long.class);
            objects.put((long) i, builder.build());
        }
        binCache.putAll(objects);
        SqlFieldsQuery sql = new SqlFieldsQuery("select id, topic, start_time from KIPOD_EVENT where topic = ?");
        sql.setArgs("Topic 4");
        binCache.query(sql).forEach(row -> {
            log.info(">>>> {}", row);
        });
    }

    @Test
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
        loadData(i);
        long dataLoadStop = System.currentTimeMillis();
        log.info("Insert total exec: {}ms", dataLoadStop - dataLoadStart);
        long startSelect = System.currentTimeMillis();
        log.info("{}", jdbcTemplate.queryForList(SELECT_EVENT));
        long endSelect = System.currentTimeMillis();
        log.info("Select exec: {}ms", endSelect - startSelect);
    }
    
    private void loadData(long startId) {
        List<List<Object>> batch = new LinkedList<>();
        long end = startId + 10_000_000;
        for (; startId <= end; startId++) {
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
            row.add(System.currentTimeMillis());
            row.add(System.currentTimeMillis());
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
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
            
        });
    }
    
    private void clearData() {
        jdbcTemplate.execute(DELETE_ALL_EVENT);
    }

}
