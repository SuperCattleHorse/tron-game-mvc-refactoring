package com.tron.model.score;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Unit tests for {@link LocalDateAdapter}.
 * 
 * Tests JSON serialization and deserialization of LocalDate objects
 * using Gson TypeAdapter.
 * 
 * @author High Score System Team
 * @version 1.0
 */
@DisplayName("LocalDateAdapter Unit Tests")
public class LocalDateAdapterTest {

    private LocalDateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalDateAdapter();
    }

    @Test
    @DisplayName("Given LocalDate, When writing to JSON, Then should serialize to ISO-8601 format")
    void testWriteLocalDate() throws IOException {
        // Given
        LocalDate date = LocalDate.of(2025, 12, 5);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        
        // When
        adapter.write(jsonWriter, date);
        jsonWriter.flush();
        
        // Then
        String result = stringWriter.toString();
        assertEquals("\"2025-12-05\"", result, "Should serialize to ISO-8601 format with quotes");
    }

    @Test
    @DisplayName("Given null date, When writing to JSON, Then should write null")
    void testWriteNullDate() throws IOException {
        // Given
        LocalDate date = null;
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        
        // When
        adapter.write(jsonWriter, date);
        jsonWriter.flush();
        
        // Then
        String result = stringWriter.toString();
        assertEquals("null", result, "Should write null for null date");
    }

    @Test
    @DisplayName("Given JSON date string, When reading, Then should deserialize to LocalDate")
    void testReadLocalDate() throws IOException {
        // Given
        String jsonDate = "\"2025-12-05\"";
        JsonReader jsonReader = new JsonReader(new StringReader(jsonDate));
        
        // When
        LocalDate result = adapter.read(jsonReader);
        
        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(2025, result.getYear(), "Year should be 2025");
        assertEquals(12, result.getMonthValue(), "Month should be 12");
        assertEquals(5, result.getDayOfMonth(), "Day should be 5");
    }

    @Test
    @DisplayName("Given different date, When serializing and deserializing, Then should preserve value")
    void testRoundTrip() throws IOException {
        // Given
        LocalDate originalDate = LocalDate.of(1970, 1, 1);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        
        // When - serialize
        adapter.write(jsonWriter, originalDate);
        jsonWriter.flush();
        
        // Then - deserialize
        String json = stringWriter.toString();
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        LocalDate deserializedDate = adapter.read(jsonReader);
        
        assertEquals(originalDate, deserializedDate, "Dates should match after round trip");
    }

    @Test
    @DisplayName("Given leap year date, When serializing, Then should handle correctly")
    void testLeapYearDate() throws IOException {
        // Given
        LocalDate leapDate = LocalDate.of(2024, 2, 29);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        
        // When
        adapter.write(jsonWriter, leapDate);
        jsonWriter.flush();
        
        // Then
        String result = stringWriter.toString();
        assertEquals("\"2024-02-29\"", result, "Should correctly serialize leap year date");
    }

    @Test
    @DisplayName("Given various dates, When converting, Then should maintain consistency")
    void testMultipleDates() throws IOException {
        // Given
        LocalDate[] dates = {
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 12, 31),
            LocalDate.of(2000, 6, 15),
            LocalDate.now()
        };
        
        // When & Then
        for (LocalDate date : dates) {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            adapter.write(jsonWriter, date);
            jsonWriter.flush();
            
            String json = stringWriter.toString();
            JsonReader jsonReader = new JsonReader(new StringReader(json));
            LocalDate deserialized = adapter.read(jsonReader);
            
            assertEquals(date, deserialized, 
                String.format("Date %s should match after serialization", date));
        }
    }
}
