package ru.synesis.kipod.event;

/**
 * 
 * @author arseny.kovalchuk
 *
 */
public interface CacheNameStrategy {

    default String cacheName(KipodEvent event) {
        return event.module + "_" +  event.channel;
    }
    
}
