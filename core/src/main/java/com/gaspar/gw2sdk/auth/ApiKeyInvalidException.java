package com.gaspar.gw2sdk.auth;

import com.gaspar.gw2sdk.SdkException;

public class ApiKeyInvalidException extends SdkException {

    public ApiKeyInvalidException(String apiKey, String reason) {
        super(String.format("The GW2 API key '%s' is invalid because: %s", apiKey, reason));
    }

    public ApiKeyInvalidException(String reason) {
        super(String.format("The GW2 API key is invalid because: %s", reason));
    }

}
