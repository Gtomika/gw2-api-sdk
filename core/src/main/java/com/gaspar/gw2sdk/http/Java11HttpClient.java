package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.Constants;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP client implementation using the built-in JDK HTTP client. This is the default
 * {@link UnderlyingHttpClient} used by the SDK.
 */
@Slf4j
public class Java11HttpClient implements UnderlyingHttpClient<HttpClient> {

    private HttpClient httpClient;

    public Java11HttpClient() {
        this.httpClient = HttpClient.newHttpClient();
        log.debug("HTTP Client initialized with default values");
    }

    @Override
    public CompletableFuture<HttpResponse> httpGetAsync(HttpRequest httpRequest) {
        try {
            java.net.http.HttpRequest request = convertGw2Request(httpRequest);
            log.debug("Making async HTTP GET request to '{}'", request.uri().toString());
            return httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenApply(this::convertGw2Response);
        } catch (Exception e) {
            log.warn("Failed to make HTTP request", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public HttpClient exposeUnderlyingHttpClient() {
        return httpClient;
    }

    @Override
    public void replaceUnderlyingHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private java.net.http.HttpRequest convertGw2Request(HttpRequest httpRequest) {
        var builder = java.net.http.HttpRequest.newBuilder()
                .GET()
                .uri(httpRequest.url());

        //headers
        httpRequest.apiKey().ifPresent(apiKey -> {
            log.debug("Setting '{}' header for request", Constants.AUTHORIZATION_HEADER);
            builder.setHeader(Constants.AUTHORIZATION_HEADER, "Bearer " + apiKey);
        });

        log.debug("Setting '{}' header for request with value '{}'", Constants.SCHEMA_VERSION_HEADER, httpRequest.schemaVersion());
        builder.header(Constants.SCHEMA_VERSION_HEADER, httpRequest.schemaVersion());

        return builder.build();
    }

    private HttpResponse convertGw2Response(java.net.http.HttpResponse<String> response) {
        return new HttpResponse(response.body(), response.statusCode());
    }
}
