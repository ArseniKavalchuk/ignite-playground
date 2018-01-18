package ru.synesis.kipod.event;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class KipodEventKeyBuilder {

    public static final String SEP = ":";

    private KipodEventKeyBuilder() {}
    
    public static String key(Object... parts) {
        return Arrays.stream(parts).map(Object::toString).collect(Collectors.joining(SEP));
    }
    
}
