package com.gaspar.gw2sdk.auth;

import com.gaspar.gw2sdk.Gw2SdkException;

public class Gw2ApiKeyInvalidException extends Gw2SdkException {

    public Gw2ApiKeyInvalidException(String apiKey, String reason) {
        super(String.format("The GW2 API key '%s' is invalid because: %s", apiKey, reason));
    }

    public Gw2ApiKeyInvalidException(String reason) {
        super(String.format("The GW2 API key is invalid because: %s", reason));
    }

}
