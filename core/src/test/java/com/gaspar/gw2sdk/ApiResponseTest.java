package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.http.HttpResponse;
import com.gaspar.gw2sdk.serialization.SdkSerializationTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldCreateSuccessfulApiResponse() throws Exception {
        SdkSerializationTest.TestData testData = new SdkSerializationTest.TestData("hello", 1);
        String serializedTestData = mapper.writeValueAsString(testData);

        var response = new ApiResponse<SdkSerializationTest.TestData>(
                Optional.of(new HttpResponse(serializedTestData, 200)),
                new TypeReference<>() {}
        );

        assertTrue(response.isSuccessful());
        assertFalse(response.isApiError());
        assertFalse(response.isNoAnswer());

        var fetchedData = response.data();
        assertEquals(testData, fetchedData);
        assertThrows(SdkException.class, response::errorData);
    }

    @Test
    public void shouldCreateErrorApiResponse() throws Exception {
        var response = new ApiResponse<>(
                Optional.of(new HttpResponse("Error!", 401)),
                new TypeReference<>() {}
        );

        assertFalse(response.isSuccessful());
        assertTrue(response.isApiError());
        assertFalse(response.isNoAnswer());

        assertEquals(new ApiErrorData("Error!", 401), response.errorData());
        assertThrows(SdkException.class, response::data);
    }

    @Test
    public void shouldCreateNoAnswerApiResponse() {
        var response = new ApiResponse<>(
                Optional.empty(),
                new TypeReference<>() {}
        );

        assertFalse(response.isSuccessful());
        assertFalse(response.isApiError());
        assertTrue(response.isNoAnswer());

        assertThrows(SdkException.class, response::data);
        assertThrows(SdkException.class, response::errorData);
    }
}