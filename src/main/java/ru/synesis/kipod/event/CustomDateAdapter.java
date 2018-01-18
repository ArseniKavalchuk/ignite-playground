package ru.synesis.kipod.event;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ru.synesis.kipod.utils.DateUtils;

public class CustomDateAdapter implements JsonDeserializer<Long>, JsonSerializer<Long> {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").withZone(ZoneOffset.UTC);

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // just for transition purpose: try to parse as long, if it is not, then try to parse
        // as stringified datetime. This class will be removed soon
        try {
            return json.getAsLong();
        } catch (ClassCastException e) {
            // do nothing, try to parse as stringified date
        } catch (NumberFormatException e) {
            // do nothing, try to parse as stringified date
        }

        Instant time = null;
        // The date comes with fucking different formats with arbitrary millis, micro, nanoseconds!
        String dateString = DateUtils.normalizeDate(json.getAsJsonPrimitive().getAsString());
        try {
            time = LocalDateTime.parse(dateString, formatter1).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e1) {
            logger.warn("Cannot parse {}", dateString);
            return 0L;
        }
        return time.toEpochMilli();
    }



}
