package ru.synesis.kipod.event;
/**
 * 
 * @author arseny.kovalchuk
 *
 */
public interface CacheAware<K> {
    
    K cacheKey();
    
    String cacheName();
    
}
