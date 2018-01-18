package ru.synesis.kipod.service;

public class CacheReadyEvent {

    private String cacheName;
    
    public CacheReadyEvent(String cacheName) {
        this.cacheName = cacheName;
    }
    
    public String getCacheName() {
        return cacheName;
    }

}
