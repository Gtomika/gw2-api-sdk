package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.annotations.SdkInternal;
import com.gaspar.gw2sdk.auth.ApiKey;
import com.gaspar.gw2sdk.InvalidParamException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This client is responsible for making HTTP calls to the GW2 API. The requests are delegated to an
 * {@link UnderlyingHttpClient}.
 */
//TODO think about retry policy
@Slf4j
@SdkInternal
public class Gw2HttpClient {

    /**
     * API base URL that will be prepended to every path.
     */
    private static final String API_BASE_URL = "https://api.guildwars2.com";

    /**
     * Underlying HTTP client implementation that will make the requests.
     */
    @Getter
    @Setter
    private UnderlyingHttpClient<?> underlyingHttpClient;

    /**
     * API key to be attached to the requests: in case it is not provided, unauthenticated requests will be sent.
     */
    @Getter
    private Optional<ApiKey> apiKey;

    /**
     * Schema version used in the API calls. Must be a valid ISO 8601 date time, or 'latest'.
     */
    @Getter
    private String schemaVersion;

    /**
     * Determines after how many seconds will the requests time out.
     */
    @Getter
    private int timeoutSeconds;

    @Builder
    private Gw2HttpClient(
            UnderlyingHttpClient<?> underlyingHttpClient,
            ApiKey apiKey,
            Integer timeoutSeconds
    ) {
        this.underlyingHttpClient = withDefaultValue(underlyingHttpClient, new Java11HttpClient());
        this.apiKey = Optional.ofNullable(apiKey);
        this.timeoutSeconds = validateTimeoutSeconds(withDefaultValue(timeoutSeconds, 5));
        this.schemaVersion = "2023-03-09T00:00:00Z"; //TODO extract this config somehow
    }

    /**
     * Get data from the GW2 API asynchronously. DO NOT use this method directly!
     * @param path API path which must not include the base URL and must begin with '/'. For example {@code /v2/account}.
     * @return A future with the response, or empty optional in case the response could not be obtained.
     * @throws HttpException If the client cannot make the request at all because of invalid path provided.
     */
    public CompletableFuture<Optional<HttpResponse>> fetchDataAsync(String path) throws HttpException {
        try {
            HttpRequest request = new HttpRequest(
                    new URI(API_BASE_URL + path),
                    apiKey.map(ApiKey::getToken),
                    schemaVersion
            );
            return underlyingHttpClient.httpGetAsync(request) //must not throw exceptions: will only return with futures
                    .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .exceptionally(this::exceptionHandlerStage) //convert exceptional future to null
                    .thenApply(Optional::ofNullable); //convert value or null to Optional
        } catch (URISyntaxException e) {
            log.error("Incorrect path provided and an URI cannot be made: {}{}", API_BASE_URL, path);
            throw new HttpException(e);
        }
    }

    private HttpResponse exceptionHandlerStage(Throwable t) {
        if(t instanceof TimeoutException) {
            log.warn("The HTTP request timed out after {} seconds, returning null...", timeoutSeconds);
            return null;
        }
        log.warn("Exception occurred in underlying HTTP client, returning null");
        return null;
    }

    private int validateTimeoutSeconds(int timeoutSeconds) {
        if(timeoutSeconds <= 0) {
            throw new InvalidParamException("timeoutSeconds", timeoutSeconds, List.of("Must be positive"));
        }
        return timeoutSeconds;
    }

    private <T> T withDefaultValue(@Nullable T value, @Nonnull T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public void setApiKey(ApiKey apiKey) {
        this.apiKey = Optional.ofNullable(apiKey);
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = validateTimeoutSeconds(timeoutSeconds);
    }
}
