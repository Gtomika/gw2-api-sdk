package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.annotations.Gw2ApiComponent;
import com.gaspar.gw2sdk.annotations.Gw2ApiOperation;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.List;

/**
 * API component for the root of the GW2 API.
 */
@Gw2ApiComponent(
        basePath = "/",
        apiKeyNeeded = false,
        requiredPermissions = {}
)
@Slf4j
public class Gw2Api extends ApiComponent {

    @Builder
    protected Gw2Api(@Nullable Gw2HttpClient gw2HttpClient) {
        super(gw2HttpClient);
    }

    /**
     * Get a list of versions that the GW2 API supports.
     */
    @Gw2ApiOperation(path = "/")
    public ApiPromise<List<String>> getVersions() {
        log.debug("Fetching all API versions...");
        var future = gw2HttpClient.fetchDataAsync("");
        return ApiPromise.of(future, new TypeReference<>() {});
    }

    /**
     * Gets information about the version 1 (v1) of the GW2 API. This information is
     * returned as a raw string by the API.
     */
    @Gw2ApiOperation(path = "/v1")
    public ApiPromise<String> getVersion1Info() {
        log.debug("Fetching API V1 information...");
        var future = gw2HttpClient.fetchDataAsync("/v1");
        return ApiPromise.of(future, new TypeReference<>() {});
    }

    /**
     * Gets information about the version 2 (v2) of the GW2 API. This information is
     * returned as a raw string by the API.
     */
    @Gw2ApiOperation(path = "/v2")
    public ApiPromise<String> getVersion2Info() {
        log.debug("Fetching API V2 information...");
        var future = gw2HttpClient.fetchDataAsync("/v2");
        return ApiPromise.of(future, new TypeReference<>() {});
    }
}
