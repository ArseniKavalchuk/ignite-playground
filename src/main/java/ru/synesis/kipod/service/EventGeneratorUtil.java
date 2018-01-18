package ru.synesis.kipod.service;

import static ru.synesis.kipod.service.EventPropertiesUtil.randomChannel;
import static ru.synesis.kipod.service.EventPropertiesUtil.randomFirstName;
import static ru.synesis.kipod.service.EventPropertiesUtil.randomLastName;
import static ru.synesis.kipod.service.EventPropertiesUtil.randomPlateNumer;
import static ru.synesis.kipod.service.EventPropertiesUtil.randomTopic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ru.synesis.kipod.event.EventConstants;
import ru.synesis.kipod.event.KipodEvent;

public interface EventGeneratorUtil {
    
    static final AtomicLong eventId = new AtomicLong();
    static final long EXP_INTERVAL = 1L * 60 * 60 * 1000; // 1 hour

    
    public static Map<String, KipodEvent> generateEvents(int i) {
        Map<String, KipodEvent> result = IntStream
            .rangeClosed(1, i)
            .mapToObj(EventGeneratorUtil::generateOneEvent)
            .sorted(EventPropertiesUtil::eventComparator)
            .collect(Collectors.toMap(
                    KipodEvent::cacheKey,
                    Function.identity(),
                    EventGeneratorUtil::merger,
                    LinkedHashMap::new));
        return result;
    }
    
    public static KipodEvent generateOneEvent(int i) {
        long startTime = System.currentTimeMillis();
        KipodEvent e = new KipodEvent().withCacheName(EventConstants.KIPOD_EVENTS);
        e.id                    = eventId.incrementAndGet();
        e.channel               = randomChannel();
        e.start_time            = startTime;
        e.end_time              = startTime + 5;
        e.topic                 = randomTopic();
        e.expiration            = startTime + EXP_INTERVAL;
        e.face_first_name       = randomFirstName();
        e.face_last_name        = randomLastName();
        e.license_plate_number  = randomPlateNumer();
        return e;
    }

    
    static KipodEvent merger(KipodEvent e1, KipodEvent e2) {
        return e1;
    }

}
