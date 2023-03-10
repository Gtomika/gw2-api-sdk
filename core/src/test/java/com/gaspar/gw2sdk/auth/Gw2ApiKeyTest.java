package com.gaspar.gw2sdk.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Gw2ApiKeyTest {

    private static final String VALID_TOKEN = "924A8073-8EF2-2046-A7C7-3BC31911DB41C021088A-3AEA-4775-8D4B-6B374B79496B";

    @Test
    public void shouldAcceptValidToken() {
        var key = Gw2ApiKey.builder()
                .token(VALID_TOKEN)
                .build();
        assertEquals(VALID_TOKEN, key.getToken());
    }

    @ParameterizedTest
    @ValueSource(strings = {"924A8073-8EF2-2046-A7C7-3BC31911DB41C021088A", "???", "API_KEY_test", ""})
    @NullSource
    public void shouldNotAcceptInvalidToken(String token) {
        assertThrows(Gw2ApiKeyInvalidException.class, () -> {
            Gw2ApiKey.builder()
                    .token(token)
                    .build();
        });
    }

    @Test
    public void shouldAcceptIfPermissionsNotProvided() {
        var key = Gw2ApiKey.builder()
                .token(VALID_TOKEN)
                .build();
        assertEquals(Gw2Permission.ALL_PERMISSIONS, key.getPermissions());
    }

    @Test
    public void shouldAcceptValidPermissionSet() {
        var permissions = Set.of(Gw2Permission.ACCOUNT, Gw2Permission.PVP);
        var key = Gw2ApiKey.builder()
                .token(VALID_TOKEN)
                .permissions(permissions)
                .build();
        assertEquals(permissions, key.getPermissions());
    }

    @Test
    public void shouldNotAcceptInvalidPermissionSet() {
        assertThrows(Gw2ApiKeyInvalidException.class, () -> {
            Gw2ApiKey.builder()
                    .token(VALID_TOKEN)
                    .permissions(Set.of(Gw2Permission.BUILDS)) //does not have 'ACCOUNT'
                    .build();
        });
    }

}