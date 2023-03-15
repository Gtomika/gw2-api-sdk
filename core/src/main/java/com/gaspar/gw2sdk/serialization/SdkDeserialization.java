package com.gaspar.gw2sdk.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.annotations.SdkInternal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SdkInternal
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SdkDeserialization {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Serialize JSON string into a data object.
     * @param content Raw content, expected in JSON format.
     * @param dataType Type of data class.
     * @throws SdkDeserializationException If serialization failed.
     */
    public static <T> T deserializeData(String content, TypeReference<T> dataType) throws SdkDeserializationException {
        try {
            if(isRawString(dataType)) {
                log.debug("Data type is raw string: no deserialization needed, skipping...");
                return (T) content;
            } else {
                return mapper.readValue(content, dataType);
            }
        } catch (Exception e) {
            log.error("Fail to serialize string '{}' into data class '{}'", content, dataType.getType().getTypeName(), e);
            throw new SdkDeserializationException(String.format(
                    "Failed to serialize raw data into object of type '%s'. Raw data:\n%s", dataType.getType().getTypeName(), content
            ));
        }
    }

    private static boolean isRawString(TypeReference<?> dataType) {
        return "java.lang.String".equals(dataType.getType().getTypeName());
    }

}
