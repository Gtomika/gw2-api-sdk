package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.http.Gw2HttpClient;
import com.gaspar.gw2sdk.http.HttpClientRequiredException;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An API component is associated with a set of GW2 API endpoints (such as 'account API').
 * This is to be used as a base class for all API components.
 */
public class ApiComponent {

    @Nonnull
    @Getter
    protected Gw2HttpClient gw2HttpClient;

    protected ApiComponent(@Nullable Gw2HttpClient gw2HttpClient) {
        this.gw2HttpClient = validateClientPresent(gw2HttpClient);
    }

    private Gw2HttpClient validateClientPresent(@Nullable Gw2HttpClient client) throws HttpClientRequiredException {
        if(client == null) {
            throw new HttpClientRequiredException(getClass().getSimpleName());
        }
        return client;
    }

}
