package com.gaspar.gw2sdk.http;

import java.util.concurrent.CompletableFuture;

/**
 * All HTTP clients used by the SDK must implement this interface. It forces underlying HTTP clients to
 * wrap their classes into SDK response/request class: {@link Gw2HttpRequest}, {@link Gw2HttpResponse}.
 * <ul>
 *     <li>Implementations must not serialize the responses: it needs to be string.</li>
 *     <li>Implementations must not throw exceptions on HTTP status codes 400, 500: this is up to the caller.</li>
 * </ul>
 * @param <ClientType> Type of the HTTP client used.
 */
public interface UnderlyingHttpClient<ClientType> {

    /**
     * Make an asynchronous HTTP GET request. This method must not throw an exception! In case of an issue with making
     * HTTP request, return {@link CompletableFuture#failedFuture(Throwable)}.
     * @param request SDK HTTP request object.
     * @return A future with the SDK HTTP response object.
     */
    CompletableFuture<Gw2HttpResponse> httpGetAsync(Gw2HttpRequest request);

    /**
     * Exposes the HTTP client used by the underlying implementation. This makes it possible to read its properties
     * or in some cases: configure it.
     */
    ClientType exposeUnderlyingHttpClient();

    /**
     * Allows to set the underlying HTTP client. This makes it possible to give full control to the user in term
     * of configuring it.
     * @param httpClient HTTP client to use for making requests.
     */
    void replaceUnderlyingHttpClient(ClientType httpClient);
}
