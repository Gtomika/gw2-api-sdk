package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.http.Gw2HttpResponse;
import com.gaspar.gw2sdk.serialization.Gw2SdkSerializationTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Gw2ApiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldCreateSuccessfulApiResponse() throws Exception {
        Gw2SdkSerializationTest.TestData testData = new Gw2SdkSerializationTest.TestData("hello", 1);
        String serializedTestData = mapper.writeValueAsString(testData);

        var response = new Gw2ApiResponse<Gw2SdkSerializationTest.TestData>(
                Optional.of(new Gw2HttpResponse(serializedTestData, 200)),
                new TypeReference<>() {}
        );

        assertTrue(response.isSuccessful());
        assertFalse(response.isApiError());
        assertFalse(response.isNoAnswer());

        var fetchedData = response.data();
        assertEquals(testData, fetchedData);
        assertThrows(Gw2ApiResponseException.class, response::errorData);
    }

    @Test
    public void shouldCreateErrorApiResponse() throws Exception {
        var response = new Gw2ApiResponse<>(
                Optional.of(new Gw2HttpResponse("Error!", 401)),
                new TypeReference<>() {}
        );

        assertFalse(response.isSuccessful());
        assertTrue(response.isApiError());
        assertFalse(response.isNoAnswer());

        assertEquals(new Gw2ApiErrorData("Error!", 401), response.errorData());
        assertThrows(Gw2ApiResponseException.class, response::data);
    }

    @Test
    public void shouldCreateNoAnswerApiResponse() {
        var response = new Gw2ApiResponse<>(
                Optional.empty(),
                new TypeReference<>() {}
        );

        assertFalse(response.isSuccessful());
        assertFalse(response.isApiError());
        assertTrue(response.isNoAnswer());

        assertThrows(Gw2ApiResponseException.class, response::data);
        assertThrows(Gw2ApiResponseException.class, response::errorData);
    }
}