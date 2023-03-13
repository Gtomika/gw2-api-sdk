package com.gaspar.gw2sdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CancellationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        setApiIdsResponse(List.of(11L, 22L, 33L));
        achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer"))
                .join();
    }

    @Test
    public void shouldCancelAndThrowExceptionOnJoin() throws Exception {
        setApiIdsResponse(List.of(11L, 22L, 33L));
        var promise = achievementsApi.getAchievementIds()
                .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
                .onError(errorData -> log.error("GW2 API error: {}", errorData))
                .onNoAnswer(() -> log.error("GW2 API failed to answer"));
        promise.cancel();
        assertThrows(CancellationException.class, promise::join);
    }

    private void setApiIdsResponse(List<Long> ids) throws Exception {
        String response = mapper.writeValueAsString(ids);
        mockClient.setMockResponse(response);
    }

}