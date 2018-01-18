package ru.synesis.kipod.service;

import java.util.concurrent.ThreadLocalRandom;

import ru.synesis.kipod.event.KipodEvent;

public interface EventPropertiesUtil {

    static final String[] TOPICS    = {"Topic1", "Topic2", "Topic3", "Topic4"};
    static final int[] N            = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    static final char[] L           = { 'A', 'B', 'C', 'E', 'H', 'I', 'K', 'M', 'O', 'P', 'T', 'X' };
    static final int[] R            = { 1, 2, 3, 4, 5, 6, 7 };
    static final String[] F_NAMES   = {
        "alvin",
        "lyle",
        "darin",
        "bertram",
        "edward",
        "graig",
        "enrique",
        "alec",
        "joshua",
        "johnny"
    };
    static final String[] L_NAMES   = {
        "shit",
        "fuck",
        "damn",
        "bitch",
        "crap",
        "piss",
        "dick",
        "darn",
        "cock",
        "pussy",
        "asshole",
        "fag",
        "bastard",
        "slut",
        "douche"
    };
    
    
    public static long randomChannel() {
        return ThreadLocalRandom.current().nextLong(1, 11);
    }
    
    public static String randomTopic() {
        return TOPICS[ThreadLocalRandom.current().nextInt(4)];
    }
    
    public static String randomPlateNumer() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        StringBuilder res = new StringBuilder()
            .append(N[rnd.nextInt(10)])
            .append(N[rnd.nextInt(10)])
            .append(N[rnd.nextInt(10)])
            .append(N[rnd.nextInt(10)])
            .append(L[rnd.nextInt(12)])
            .append(L[rnd.nextInt(12)])
            .append(R[rnd.nextInt(7)]);
        return res.toString();
    }
    
    public static String randomFirstName() {
        return F_NAMES[ThreadLocalRandom.current().nextInt(10)];
    }
    
    public static String randomLastName() {
        return L_NAMES[ThreadLocalRandom.current().nextInt(10)];
    }

    public static int eventComparator(KipodEvent e1, KipodEvent e2) {
        if (e1 == null && e2 == null) return 0;
        if (e1 == null || e2 == null) return -1;
        return e1.cacheKey().compareTo(e2.cacheKey());
    }
    
}
