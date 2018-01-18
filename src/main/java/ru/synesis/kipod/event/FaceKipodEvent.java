package ru.synesis.kipod.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FaceKipodEvent implements ExpireAware, CacheAware<String>, Serializable {
    
    private static final long serialVersionUID = 0L;
    
    private String cacheKey;
    private String cacheName;

    public Long id;
    public Long start_time;
    public Long channel;
    public String descriptor_version;
    public List<float[]> descriptors = new ArrayList<>();
    public Long kafka_offset;
    public Long expiration;

    public static FaceKipodEvent create(
            String cacheName,
            long channel,
            long startTime, 
            long id,
            long kafkaOffset,
            String descriptorVersion,
            long expiration,
            List<float[]> descriptors) {
        FaceKipodEvent faceDetectedEvent = new FaceKipodEvent();
        faceDetectedEvent.cacheName = cacheName;
        faceDetectedEvent.cacheKey = KipodEventKeyBuilder.key(channel, startTime, id);
        faceDetectedEvent.channel = channel;
        faceDetectedEvent.start_time = startTime;
        faceDetectedEvent.id = id;
        faceDetectedEvent.kafka_offset = kafkaOffset;
        faceDetectedEvent.descriptor_version = descriptorVersion;
        faceDetectedEvent.expiration = expiration;
        faceDetectedEvent.descriptors = descriptors;
        return faceDetectedEvent;
    }

    @Override
    public String cacheKey() {
        if (this.cacheKey == null) 
            this.cacheKey = KipodEventKeyBuilder.key(this.channel, this.start_time, this.id);
        return this.cacheKey;
    }

    @Override
    public String cacheName() {
        return this.cacheName;
    }

    @Override
    public String toString() {
        return "FaceDetectedEvent [id=" + id
                + ", start_time=" + start_time
                + ", channel=" + channel
                + ", kafka_offset=" + kafka_offset
                + ", expiration=" + expiration
                + ", descriptor_version=" + descriptor_version 
                + ", descriptors = size(" + descriptors == null ? "0": descriptors.size() + ")]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (channel ^ (channel >>> 32));
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (start_time ^ (start_time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FaceKipodEvent other = (FaceKipodEvent) obj;
        if (channel != other.channel)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (start_time != other.start_time)
            return false;
        return true;
    }

    @Override
    public Long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
    
}
