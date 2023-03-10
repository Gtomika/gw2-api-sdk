package com.gaspar.gw2sdk;

/**
 * Thrown when you attempt to retrieve data from the API response that is not present.
 * This exception is <b>checked</b>, because the API is notoriously unreliable and
 * any code calling should always expect errors.
 */
public class Gw2ApiResponseException extends Gw2SdkCheckedException {

    public Gw2ApiResponseException(String message) {
        super(message);
    }
}
