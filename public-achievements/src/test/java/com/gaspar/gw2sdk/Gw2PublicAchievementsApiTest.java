package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class Gw2PublicAchievementsApiTest {

    private Gw2PublicAchievementsApi achievementsApi;

    @BeforeEach
    public void setUp() {
        Gw2HttpClient httpClient = Gw2HttpClient.builder()
                .build();
        achievementsApi = Gw2PublicAchievementsApi.builder()
                .gw2HttpClient(httpClient)
                .build();
    }

    @Test
    public void shouldGetList() {
        achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer"))
                .join();
    }

    @Test
    public void shouldCancel() {
        var promise = achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer"));
        promise.cancel();
        promise.join();
    }

}