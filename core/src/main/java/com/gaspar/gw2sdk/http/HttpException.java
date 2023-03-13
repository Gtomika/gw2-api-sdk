package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.SdkException;

public class HttpException extends SdkException {

    public HttpException(Exception cause) {
        super("Exception while making HTTP request", cause);
    }
}
