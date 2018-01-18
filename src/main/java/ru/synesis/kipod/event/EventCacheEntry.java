package ru.synesis.kipod.event;

import java.util.Map;

/**
 * 
 * @author arseny.kovalchuk
 *
 */
public class EventCacheEntry<K, V> implements Map.Entry<K, V> {

    private K key;
    private V val;

    public EventCacheEntry(K key, V val) {
        this.key = key;
        this.val = val;
    }
    
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return val;
    }

    @Override
    public V setValue(V value) {
        this.val = value;
        return val;
    }

}
