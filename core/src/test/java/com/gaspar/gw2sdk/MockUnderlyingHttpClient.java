package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.http.Gw2HttpRequest;
import com.gaspar.gw2sdk.http.Gw2HttpResponse;
import com.gaspar.gw2sdk.http.UnderlyingHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class MockUnderlyingHttpClient implements UnderlyingHttpClient<HttpClient> {

    private static final String MOCK_RESPONSE = """
            {
                "text": "Some API response"
            }
            """;

    @Override
    public CompletableFuture<Gw2HttpResponse> httpGetAsync(Gw2HttpRequest request) {
        log.debug("Mocking HTTP request to '{}'", request.url().toString());
        return CompletableFuture.completedFuture(new Gw2HttpResponse(MOCK_RESPONSE, 200));
    }

    @Override
    public HttpClient exposeUnderlyingHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Override
    public void replaceUnderlyingHttpClient(HttpClient httpClient) {}
}
