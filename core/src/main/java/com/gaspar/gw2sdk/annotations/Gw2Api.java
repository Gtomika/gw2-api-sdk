package com.gaspar.gw2sdk.annotations;

import com.gaspar.gw2sdk.auth.Gw2Permission;

import java.util.Set;

public @interface Gw2Api {

    /**
     * Path to the API, not including the base URL. Must begin with '/'.
     */
    String path();

    /**
     * If an API key is required to access this API.
     */
    boolean apiKeyNeeded();

    /**
     * In case of {@link #apiKeyNeeded()}, these are the required permissions.
     */
    Gw2Permission[] requiredPermissions();
}
