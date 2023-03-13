package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.annotations.SdkInternal;

/**
 * Raw response from the API.
 */
@SdkInternal
public record HttpResponse(
        String content,
        int statusCode
) {
}
