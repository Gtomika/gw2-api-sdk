package com.gaspar.gw2sdk.http;

import com.gaspar.gw2sdk.Gw2SdkException;

public class Gw2HttpException extends Gw2SdkException {

    public Gw2HttpException(Exception cause) {
        super("Exception while making HTTP request", cause);
    }
}
