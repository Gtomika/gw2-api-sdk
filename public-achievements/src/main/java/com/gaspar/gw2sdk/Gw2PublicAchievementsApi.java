package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.annotations.Gw2ApiComponent;
import com.gaspar.gw2sdk.annotations.Gw2ApiOperation;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Wrapper to access the GW2 public achievements API. This includes
 * info about achievements, dailies and categories. This information is not
 * tied to any account, and so it is public and accessible with an API key.
 * <p>
 * Note that only "whitelisted" achievements are available: ones that were
 * obtained by at least 1 player.
 */
@Slf4j
@Gw2ApiComponent(
        basePath = "/v2/achievements",
        apiKeyNeeded = false,
        requiredPermissions = {}
)
public class Gw2PublicAchievementsApi extends ApiComponent {

    private static final String PUBLIC_ACHIEVEMENTS_PATH = "/v2/achievements";

    @Builder
    private Gw2PublicAchievementsApi(Gw2HttpClient gw2HttpClient) {
        super(gw2HttpClient);
        log.debug("Public achievements API initialized");
    }

    /**
     * Query the list of all whitelisted achievement IDs.
     */
    @Gw2ApiOperation(path = "/v2/achievements")
    public ApiPromise<List<Long>> getAchievementIds() {
        var future = gw2HttpClient.fetchDataAsync(PUBLIC_ACHIEVEMENTS_PATH);
        return ApiPromise.of(future, new TypeReference<>() {});
    }

    //TODO get a single achievement by id
    //TODO get a list of achievements by ID list
}
