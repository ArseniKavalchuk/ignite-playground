package ru.synesis.kipod.event;

public interface ExpireAware {

    Long getExpiration();
    
    void setExpiration(Long expiration);
    
}
