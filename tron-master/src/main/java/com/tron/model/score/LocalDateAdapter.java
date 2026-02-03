package com.tron.model.score;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * LocalDateAdapter - Gson TypeAdapter for LocalDate serialization/deserialization
 * 
 * Handles conversion between LocalDate objects and JSON string format.
 * Uses ISO 8601 date format (yyyy-MM-dd).
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(FORMATTER));
        }
    }
    
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String dateString = in.nextString();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, FORMATTER);
    }
}
