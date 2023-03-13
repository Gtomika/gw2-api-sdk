package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.SdkException;

/**
 * Thrown when you attempt to create an API component without providing an HTTP client to it.
 */
public class HttpClientRequiredException extends SdkException {

    public HttpClientRequiredException(String componentName) {
        super(String.format("Cannot create API component '%s' without GW2 HTTP client!" +
                " Use the builder to provide one!", componentName));
    }
}
