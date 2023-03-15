package com.gaspar.gw2sdk.annotations;

import com.gaspar.gw2sdk.auth.ApiPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

/**
 * Methods annotated with this perform a GW2 API operation. Methods that access multiple APIs should be
 * marked with repeated instances of this annotation.
 */
@Target(ElementType.METHOD)
@Repeatable(Gw2ApiOperations.class)
public @interface Gw2ApiOperation {

    /**
     * Path to the API, not including the base URL. Must begin with '/'.
     */
    String path();

    /**
     * If an API key is required to access this API. If it's the same as the container {@link Gw2ApiComponent#apiKeyNeeded()},
     * then it should be left empty.
     */
    boolean apiKeyNeeded() default false;

    /**
     * In case of {@link #apiKeyNeeded()}, these are the required permissions. If it's the same as the
     * container {@link Gw2ApiComponent#requiredPermissions()}, then it should be left empty.
     */
    ApiPermission[] requiredPermissions() default {};
}
