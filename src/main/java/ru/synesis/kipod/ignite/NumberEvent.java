package ru.synesis.kipod.ignite;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class NumberEvent {

    @QuerySqlField(index = true)
    public long id;

    @QuerySqlField(index = true)
    public String topic;
    
    @QuerySqlField(index = true)
    public long start_time;

    @QuerySqlField
    public long end_time;
    
    @QuerySqlField(index = true)
    public String num_val;

    public static NumberEvent create(long id, String topic, long startTs, long endTs, String numVal) {
        NumberEvent res = new NumberEvent();
        res.id = id;
        res.topic = topic;
        res.start_time = startTs;
        res.end_time = endTs;
        res.num_val = numVal;
        return res;
    }
    
}
