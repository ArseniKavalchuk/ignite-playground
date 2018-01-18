package ru.synesis.kipod.event;

import java.io.Serializable;

import com.google.gson.JsonElement;

/**
 * Common event fields but without ID. Because of ugly approach having different IDs at backend and UI.
 * Backend has long ID. But UI has compound string ID channel:start_time:id
 * 
 * Also there are some differences in timestamp fileds. See descendant events
 * 
 * @see KipodEvent
 * @see UIKipodEvent
 * 
 * @author arseny
 *
 */
public abstract class AbstractEvent implements ExpireAware, Serializable {

    private static final long serialVersionUID = 0L;

    public String topic;
    
    public String module;
    
    public int level;
    
    public String source;
    
    public JsonElement params;

    public long channel;
    
    public long stream;
    
    public boolean alarm;
    
    public boolean processed;
    
    public String comment;
    
    public boolean armed;
    
    public boolean persistent;
    
    public Long kafka_offset;
    
    public Long expiration;
    
    @Override
    public Long getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

}
