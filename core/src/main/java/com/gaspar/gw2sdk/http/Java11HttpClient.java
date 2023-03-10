package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.Constants;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Java11HttpClient implements UnderlyingHttpClient<HttpClient> {

    private HttpClient httpClient;

    public Java11HttpClient() {
        this.httpClient = HttpClient.newHttpClient();
        log.debug("HTTP Client initialized with default values");
    }

    @Override
    public CompletableFuture<Gw2HttpResponse> httpGetAsync(Gw2HttpRequest gw2HttpRequest) {
        try {
            HttpRequest request = convertGw2Request(gw2HttpRequest);
            log.debug("Making async HTTP GET request to '{}'", request.uri().toString());
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
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

    private HttpRequest convertGw2Request(Gw2HttpRequest gw2HttpRequest) {
        var builder = HttpRequest.newBuilder()
                .GET()
                .uri(gw2HttpRequest.url());

        //headers
        gw2HttpRequest.apiKey().ifPresent(apiKey -> {
            log.debug("Setting '{}' header for request", Constants.AUTHORIZATION_HEADER);
            builder.setHeader(Constants.AUTHORIZATION_HEADER, "Bearer " + apiKey);
        });

        log.debug("Setting '{}' header for request with value '{}'", Constants.SCHEMA_VERSION_HEADER, gw2HttpRequest.schemaVersion());
        builder.header(Constants.SCHEMA_VERSION_HEADER, gw2HttpRequest.schemaVersion());

        return builder.build();
    }

    private Gw2HttpResponse convertGw2Response(HttpResponse<String> response) {
        return new Gw2HttpResponse(response.body(), response.statusCode());
    }
}
