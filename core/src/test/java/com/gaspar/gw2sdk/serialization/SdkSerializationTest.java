package com.gaspar.gw2sdk.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SdkSerializationTest {

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

        TestData deserializedData = SdkSerialization.deserializeJson(serialized, new TypeReference<>() {});
        assertEquals(testData, deserializedData);
    }

    @Test
    public void shouldNotSerializeInvalidJson() {
        assertThrows(SdkSerializationException.class, () -> {
            SdkSerialization.deserializeJson("invalid", new TypeReference<>() {});
        });
    }

}