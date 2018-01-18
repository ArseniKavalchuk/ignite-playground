package ru.synesis.kipod.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author arseny.kovalchuk
 *
 */
public final class DateUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class.getName());
    
    private DateUtils() {}
    
    private static final String EPOCH_BEGIN = "1970-01-01T00:00:00.000000Z";
    private static final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    
    public static String normalizeDate(String dateString) {
        if (dateString == null || dateString.length() == 0) return EPOCH_BEGIN;
        // cut Z
        dateString = dateString.endsWith("Z") ? dateString.substring(0, dateString.length() - 1) : dateString;
        // split by fraction of a second
        String[] parts = dateString.split("\\.");
        if (parts.length > 1) {
            String secondFraction = null;
            if (parts[1].length() > 6) {
                secondFraction = parts[1].substring(0, 6);
            } else {
                StringBuffer buf = new StringBuffer(6);
                buf.append(parts[1]);
                for (int i = 0; i < (6 - parts[1].length()); i++) {
                    buf.append('0');
                }
                secondFraction = buf.toString();
            }
            return parts[0] + "." + secondFraction + "Z";
        } else {
            return parts[0] + ".000000Z";
        }
    }
    
    public static Instant parseDate(String dateStr) {
        if (dateStr == null) return null;
        Instant time = null;
        String dateString = DateUtils.normalizeDate(dateStr);
        try {
            time = LocalDateTime.parse(dateString, formatter1).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            if (logger.isWarnEnabled()) logger.warn("Cannot parse {}", dateString);
            time = Instant.ofEpochMilli(0);
        }
        return time;
    }
    

    public static void main(String[] a) {
        System.out.println(normalizeDate("2017-06-07T00:00:00.123456Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00.123456"));
        System.out.println(normalizeDate("2017-06-07T00:00:00.123Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00.12Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00.1Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00"));
        System.out.println(normalizeDate("2017-06-07T00:00:00Z"));
        System.out.println(normalizeDate("2017-06-07T00:00:00.1234567890Z"));
    }
    

}
