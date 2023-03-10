package com.gaspar.gw2sdk;

public class Gw2SdkCheckedException extends Exception {

    public Gw2SdkCheckedException(String message) {
        super(message);
    }

    public Gw2SdkCheckedException(String message, Throwable cause) {
        super(message, cause);
    }

}
