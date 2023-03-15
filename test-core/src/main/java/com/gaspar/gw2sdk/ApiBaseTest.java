package com.gaspar.gw2sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import org.junit.jupiter.api.BeforeEach;

/**
 * All classes unit testing API components should be subclasses from this one.
 * Provides mock setup for HTTP calls.
 */
public class ApiBaseTest {

    protected MockUnderlyingHttpClient mockClient;

    protected Gw2HttpClient gw2HttpClient;

    protected ObjectMapper mapper = new ObjectMapper();

    //will reset response
    @BeforeEach
    public void setUpMockClient() {
        mockClient = new MockUnderlyingHttpClient();
        gw2HttpClient = Gw2HttpClient.builder()
                .underlyingHttpClient(mockClient)
                .build();
    }

}
