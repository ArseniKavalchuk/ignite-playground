package ru.synesis.kipod.event;
/**
 *
 * @author arseny.kovalchuk
 *
 */

import java.io.Serializable;
import java.util.Arrays;

import com.google.gson.annotations.JsonAdapter;

public class KipodEvent extends AbstractEvent implements CacheAware<String>, Serializable, Cloneable {

    private static final long serialVersionUID = 0L;

    private String cacheKey;
    private String cacheName;

    public long id;

    @JsonAdapter(CustomDateAdapter.class)
    public long start_time;

    @JsonAdapter(CustomDateAdapter.class)
    public long end_time;

    @JsonAdapter(CustomDateAdapter.class)
    public long commented_at;

    @JsonAdapter(CustomDateAdapter.class)
    public long processed_at;

    public String license_plate_country;

    public String license_plate_number;

    public String license_plate_first_name;

    public String license_plate_last_name;

    public Integer[] license_plate_lists;

    public Long face_list_id;

    public Long face_id;

    public String face_first_name;

    public String face_last_name;

    public String face_full_name;

    public Float face_similarity;

    public String original_id;

    public String[] descriptors;
    
    public String descriptor_version;

    public KipodEvent withCacheName(String cacheName) {
        this.cacheName = cacheName;
        return this;
    }

    public KipodEvent withCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    @Override
    public String cacheName() {
        return this.cacheName;
    }

    @Override
    public String cacheKey() {
        if (this.cacheKey == null)
            this.cacheKey = KipodEventKeyBuilder.key(this.channel, this.start_time, this.id);
        return this.cacheKey;
    }

    @Override
    public KipodEvent clone() {
        try {
            KipodEvent event = (KipodEvent) super.clone();
            if (license_plate_lists != null) event.license_plate_lists = license_plate_lists.clone();
            return event;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) channel;
        result = prime * result + (int) (end_time ^ (end_time >>> 32));
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (kafka_offset ^ (kafka_offset >>> 32));
        result = prime * result + ((module == null) ? 0 : module.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + (int) (start_time ^ (start_time >>> 32));
        result = prime * result + (int) stream;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
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
        KipodEvent other = (KipodEvent) obj;
        if (channel != other.channel)
            return false;
        if (end_time != other.end_time)
            return false;
        if (id != other.id)
            return false;
        if (kafka_offset != other.kafka_offset)
            return false;
        if (module == null) {
            if (other.module != null)
                return false;
        } else if (!module.equals(other.module))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (start_time != other.start_time)
            return false;
        if (stream != other.stream)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KipodEvent [cacheKey=");
        builder.append(cacheKey());
        builder.append(", cacheName=");
        builder.append(cacheName());
        builder.append(", id=");
        builder.append(id);
        builder.append(", start_time=");
        builder.append(start_time);
        builder.append(", end_time=");
        builder.append(end_time);
        builder.append(", commented_at=");
        builder.append(commented_at);
        builder.append(", processed_at=");
        builder.append(processed_at);
        builder.append(", license_plate_country=");
        builder.append(license_plate_country);
        builder.append(", license_plate_number=");
        builder.append(license_plate_number);
        builder.append(", license_plate_first_name=");
        builder.append(license_plate_first_name);
        builder.append(", license_plate_last_name=");
        builder.append(license_plate_last_name);
        builder.append(", license_plate_lists=");
        builder.append(Arrays.toString(license_plate_lists));
        builder.append(", face_list_id=");
        builder.append(face_list_id);
        builder.append(", face_id=");
        builder.append(face_id);
        builder.append(", face_first_name=");
        builder.append(face_first_name);
        builder.append(", face_last_name=");
        builder.append(face_last_name);
        builder.append(", face_full_name=");
        builder.append(face_full_name);
        builder.append(", face_similarity=");
        builder.append(face_similarity);
        builder.append(", original_id=");
        builder.append(original_id);
        builder.append(", descriptor_version=");
        builder.append(descriptor_version);
        builder.append(", topic=");
        builder.append(topic);
        builder.append(", module=");
        builder.append(module);
        builder.append(", level=");
        builder.append(level);
        builder.append(", source=");
        builder.append(source);
        builder.append(", params=");
        builder.append(params);
        builder.append(", channel=");
        builder.append(channel);
        builder.append(", stream=");
        builder.append(stream);
        builder.append(", alarm=");
        builder.append(alarm);
        builder.append(", processed=");
        builder.append(processed);
        builder.append(", comment=");
        builder.append(comment);
        builder.append(", armed=");
        builder.append(armed);
        builder.append(", persistent=");
        builder.append(persistent);
        builder.append(", kafka_offset=");
        builder.append(kafka_offset);
        builder.append(", expiration=");
        builder.append(expiration);
        builder.append("]");
        return builder.toString();
    }


}
