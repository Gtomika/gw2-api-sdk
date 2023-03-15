package com.gaspar.gw2sdk.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SdkDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class TestData {
        String x;
        int y;
    }

    @Test
    public void shouldSerializeJson() throws Exception {
        TestData testData = new TestData("OK", 2);
        String serialized = mapper.writeValueAsString(testData);

        TestData deserializedData = SdkDeserialization.deserializeData(serialized, new TypeReference<>() {});
        assertEquals(testData, deserializedData);
    }

    @Test
    public void shouldNotSerializeInvalidJson() {
        assertThrows(SdkDeserializationException.class, () -> {
            SdkDeserialization.deserializeData("invalid", new TypeReference<>() {});
        });
    }

    @Test
    public void shouldDeserializeRawString() throws Exception {
        String testData = "hello";
        String deserializedTestData = SdkDeserialization.deserializeData(testData, new TypeReference<>() {});
        assertEquals(testData, deserializedTestData);
    }

}