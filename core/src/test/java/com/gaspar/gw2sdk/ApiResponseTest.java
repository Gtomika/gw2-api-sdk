package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.http.HttpResponse;
import com.gaspar.gw2sdk.serialization.SdkDeserializationTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldCreateSuccessfulApiResponse() throws Exception {
        SdkDeserializationTest.TestData testData = new SdkDeserializationTest.TestData("hello", 1);
        String serializedTestData = mapper.writeValueAsString(testData);

        var response = new ApiResponse<SdkDeserializationTest.TestData>(
                Optional.of(new HttpResponse(serializedTestData, 200)),
                new TypeReference<>() {}
        );

        assertTrue(response.isSuccessful());
        assertFalse(response.isApiError());
        assertFalse(response.isNoAnswer());

        var fetchedData = response.data().orElseThrow(AssertionError::new);
        assertEquals(testData, fetchedData);
        assertTrue(response.errorData().isEmpty());
    }

    @Test
    public void shouldCreateErrorApiResponse() {
        var response = new ApiResponse<>(
                Optional.of(new HttpResponse("Error!", 401)),
                new TypeReference<>() {}
        );

        assertFalse(response.isSuccessful());
        assertTrue(response.isApiError());
        assertFalse(response.isNoAnswer());

        var errorData = response.errorData().orElseThrow(AssertionError::new);
        assertEquals(new ApiErrorData("Error!", 401), errorData);
        assertTrue(response.data().isEmpty());
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

        assertTrue(response.errorData().isEmpty());
        assertTrue(response.data().isEmpty());
    }
}