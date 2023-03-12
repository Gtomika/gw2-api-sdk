package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        achievementsApi.getAchievementIds(new Gw2ApiCallbacks<>() {
            @Override
            public void onSuccess(List<Long> data) {
                log.info("GW2 has {} whitelisted achievements", data.size());
            }

            @Override
            public void onError(Gw2ApiErrorData errorData) {
                log.error("GW2 API has failed to answer: {}", errorData);
            }

            @Override
            public void onNoAnswer() {
                log.error("The GW2 API has not answered");
            }
        }).join();
    }

}