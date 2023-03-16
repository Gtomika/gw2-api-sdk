package com.gaspar.gw2sdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CancellationException;

@Slf4j
class Gw2PublicAchievementsApiTest extends ApiBaseTest {

    private Gw2PublicAchievementsApi achievementsApi;

    @BeforeEach
    public void setUp() {
        achievementsApi = Gw2PublicAchievementsApi.builder()
                .gw2HttpClient(gw2HttpClient)
                .build();
    }

    @Test
    public void shouldGetList() throws Exception {
        setMockResponse(List.of(11L, 22L, 33L));
        PromiseTester.of(achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer")))
                .waitForCompletion()
                .assertSuccessful();
    }

    @Test
    public void shouldNotGetListInCaseOfError() throws Exception {
        setMockErrorResponse("Internal server error", 500);
        PromiseTester.of(achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer")))
                .waitForCompletion()
                .assertApiError(500);
    }

}