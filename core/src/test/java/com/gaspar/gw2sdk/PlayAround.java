package com.gaspar.gw2sdk;

import com.gaspar.gw2sdk.auth.Gw2ApiKey;
import com.gaspar.gw2sdk.auth.Gw2Permission;
import com.gaspar.gw2sdk.http.Gw2HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PlayAround {

    @Test
    public void test() throws Exception {
        Gw2ApiKey apiKey = Gw2ApiKey.builder()
                .token("25DAF507-E323-B945-A696-91E475EFCA034A2876F1-DFD1-49A5-BF88-F7E2E7C0F6B3")
                .permissions(Gw2Permission.ALL_PERMISSIONS)
                .build();

        Gw2HttpClient client = Gw2HttpClient.builder()
                .timeoutSeconds(5)
                .apiKey(apiKey)
                .build();

        client.fetchDataAsync("/v2/account").get()
                .ifPresentOrElse(response -> {
                    log.info("Response body: {}", response.content());
                }, () -> {
                    log.error("Failed to get response");
                });

    }

}
