package com.gaspar.gw2sdk;

public class Gw2SdkException extends RuntimeException {

    public Gw2SdkException(String message) {
        super(message);
    }

    public Gw2SdkException(String message, Throwable cause) {
        super(message, cause);
    }
}
