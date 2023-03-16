package com.gaspar.gw2sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@UseRealApi
class Gw2ApiTest extends ApiBaseTest {

    private Gw2Api gw2Api;

    @BeforeEach
    public void setUp() {
        gw2Api = Gw2Api.builder()
                .gw2HttpClient(gw2HttpClient)
                .build();
    }

    @Test
    public void shouldGetVersions() {

    }

}