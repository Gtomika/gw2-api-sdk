package com.gaspar.gw2sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.dto.ErrorTextDto;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import com.gaspar.gw2sdk.http.Java11HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

/**
 * All classes unit testing API components should be subclasses from this one.
 * Provides mock setup for HTTP calls.
 */
@Slf4j
public class ApiBaseTest {

    private MockUnderlyingHttpClient mockClient;

    protected Gw2HttpClient gw2HttpClient;

    private ObjectMapper mapper = new ObjectMapper();

    //will reset response
    @BeforeEach
    public void setUpMockClient() {
        var clientBuilder = Gw2HttpClient.builder();

        if(useRealApi()) {
            log.warn("Running tests against the real GW2 API!");
            clientBuilder.underlyingHttpClient(new Java11HttpClient());
        } else {
            mockClient = new MockUnderlyingHttpClient();
            mockClient.setDelayMillis(100L);
            clientBuilder.underlyingHttpClient(mockClient);
        }

        gw2HttpClient = clientBuilder.build();
    }

    private boolean useRealApi() {
        return this.getClass().isAnnotationPresent(UseRealApi.class);
    }

    /**
     * Set mock response to be returned by all calls in current test. This method is
     * ignored if tests are running against real API.
     */
    protected <T> void setMockResponse(T t) throws Exception {
        if(useRealApi()) {
            log.debug("Ignoring mock response because running against real API!");
            return;
        }

        if(t.getClass().equals(String.class)) {
            mockClient.setMockResponse(t.toString());
        } else {
            mockClient.setMockResponse(mapper.writeValueAsString(t));
        }
    }

    /**
     * Set a mock error response to be returned by all calls in current test. This method is
     * ignored if tests are running against real API.
     */
    protected void setMockErrorResponse(String errorMessage, int statusCode) throws Exception {
        if(useRealApi()) {
            log.debug("Ignoring mock error response because running against real API!");
            return;
        }

        var error = new ErrorTextDto(errorMessage);
        mockClient.setMockResponse(mapper.writeValueAsString(error), statusCode);
    }
}
