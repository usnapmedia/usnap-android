package com.samsao.snapzi.api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * @author jfcartier
 * @since 15-04-25
 */
public class CustomJsonDateTimeDeserializer extends JsonDeserializer<DateTime> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public CustomJsonDateTimeDeserializer() {
    }

    @Override
    public DateTime deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
        try {
            return getDateFormatter().parseDateTime(jsonparser.getText());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the date formatter for the backend
     *
     * @return
     */
    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormat.forPattern(DATE_FORMAT);
    }
}
