package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.http.HttpRequest;
import com.gaspar.gw2sdk.http.HttpResponse;
import com.gaspar.gw2sdk.http.UnderlyingHttpClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Client used in unit test which does not do real HTTP calls, but returns the
 * mocked data it was provided. Do not make mock HTTP calls before setting some mock data
 * with {@link #setMockResponse(String, int)}.
 *
 * This class is not thread safe!
 */
@Slf4j
public class MockUnderlyingHttpClient implements UnderlyingHttpClient<Void> {

    @Getter
    private HttpResponse mockResponse;

    /**
     * Set a new mock response to be used by the fake client.
     */
    public void setMockResponse(String mockContent, int statusCode) {
        this.mockResponse = new HttpResponse(mockContent, statusCode);
    }

    /**
     * Set a new successful mock response used by the fake client.
     */
    public void setMockResponse(String mockContent) {
        setMockResponse(mockContent, 200);
    }

    @Override
    public CompletableFuture<HttpResponse> httpGetAsync(HttpRequest request) {
        log.debug("Mocking HTTP request to '{}'", request.url().toString());
        return CompletableFuture.completedFuture(mockResponse);
    }

    //does nothing
    @Override
    public Void exposeUnderlyingHttpClient() {
        return null;
    }

    //does nothing
    @Override
    public void replaceUnderlyingHttpClient(Void v) {}
}
