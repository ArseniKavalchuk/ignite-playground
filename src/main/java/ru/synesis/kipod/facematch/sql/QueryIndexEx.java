package ru.synesis.kipod.facematch.sql;

import java.util.LinkedHashMap;

import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.QueryIndexType;

public class QueryIndexEx extends QueryIndex {

    private static final long serialVersionUID = 1L;

    public QueryIndexEx() {
        super();
    }
    
    public QueryIndexEx(LinkedHashMap<String, Boolean> fields, QueryIndexType type, String indexName) {
        super(fields, type);
        this.setName(indexName);
    }
}
