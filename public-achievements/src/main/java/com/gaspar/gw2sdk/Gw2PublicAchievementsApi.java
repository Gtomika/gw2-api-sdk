package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.annotations.Gw2Api;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper to access the GW2 public achievements API. This includes
 * info about achievements, dailies and categories. This information is not
 * tied to any account, and so it is public and accessible with an API key.
 * <p>
 * Note that only "whitelisted" achievements are available: ones that were
 * obtained by at least 1 player.
 */
@Slf4j
@Builder
public class Gw2PublicAchievementsApi {

    private static final String PUBLIC_ACHIEVEMENTS_PATH = "/v2/achievements";

    private final Gw2HttpClient gw2HttpClient;

    //TODO get a single achievement by id
    //TODO get a list of achievements by ID list

    private Gw2PublicAchievementsApi(Gw2HttpClient gw2HttpClient) {
        Objects.requireNonNull(gw2HttpClient);
        this.gw2HttpClient = gw2HttpClient;
        log.debug("Public achievements API initialized");
    }

    /**
     * Query the list of all whitelisted achievement IDs.
     */
    @Gw2Api(
            path = PUBLIC_ACHIEVEMENTS_PATH,
            apiKeyNeeded = false,
            requiredPermissions = {}
    )
    public Gw2ApiPromise<List<Long>> getAchievementIds(Gw2ApiCallbacks<List<Long>> callbacks) {
        var future = gw2HttpClient.fetchDataAsync(PUBLIC_ACHIEVEMENTS_PATH);
        var reference = new TypeReference<List<Long>>() {};
        return new Gw2ApiPromise<>(future, reference, callbacks);
    }

}
