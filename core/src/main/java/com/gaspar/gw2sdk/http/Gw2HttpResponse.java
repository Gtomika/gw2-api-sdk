package com.gaspar.gw2sdk.http;

/**
 * Raw response from the API.
 */
public record Gw2HttpResponse(
        String content,
        int statusCode
) {
}
