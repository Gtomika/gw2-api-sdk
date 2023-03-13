package com.gaspar.gw2sdk;

public class SdkCheckedException extends Exception {

    public SdkCheckedException(String message) {
        super(message);
    }

    public SdkCheckedException(String message, Throwable cause) {
        super(message, cause);
    }

}
