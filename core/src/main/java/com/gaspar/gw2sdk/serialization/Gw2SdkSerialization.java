package com.gaspar.gw2sdk.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Gw2SdkSerialization {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Serialize JSON string into a data object.
     * @param content Raw content, expected in JSON format.
     * @param dataType Type of data class.
     * @throws Gw2SdkSerializationException If serialization failed.
     */
    public static <T> T deserializeJson(String content, TypeReference<T> dataType) throws Gw2SdkSerializationException {
        try {
            return mapper.readValue(content, dataType);
        } catch (Exception e) {
            log.error("Fail to serialize string '{}' into data class '{}'", content, dataType.getType().getTypeName(), e);
            throw new Gw2SdkSerializationException(String.format(
                    "Failed to serialize raw data into object of type '%s'. Raw data:\n%s", dataType.getType().getTypeName(), content
            ));
        }
    }

}
