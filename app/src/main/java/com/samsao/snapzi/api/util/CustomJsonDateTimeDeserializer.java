package com.samsao.snapzi.api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

/**
 * @author jfcartier
 * @since 15-04-25
 */
public class CustomJsonDateTimeDeserializer extends JsonDeserializer<DateTime>
{
    @Override
    public DateTime deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
        try {
            return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(jsonparser.getText());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
