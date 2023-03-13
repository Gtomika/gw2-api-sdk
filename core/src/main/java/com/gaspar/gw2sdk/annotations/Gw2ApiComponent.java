package com.gaspar.gw2sdk.annotations;

import com.gaspar.gw2sdk.auth.ApiPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Classes that are {@link com.gaspar.gw2sdk.ApiComponent}s should be annotated with this.
 */
@Target(ElementType.TYPE)
public @interface Gw2ApiComponent {

    /**
     * The base path of the GW2 API that this component covers. Should begin with '{@code /}'.
     * For example '{@code /v2/account}'
     */
    String basePath();

    /**
     * If an API key is required to access this API.
     */
    boolean apiKeyNeeded();

    /**
     * In case of {@link #apiKeyNeeded()}, these are the required permissions.
     */
    ApiPermission[] requiredPermissions();

}
