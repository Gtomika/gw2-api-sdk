package com.gaspar.gw2sdk.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

    private static final String VALID_TOKEN = "924A8073-8EF2-2046-A7C7-3BC31911DB41C021088A-3AEA-4775-8D4B-6B374B79496B";

    @Test
    public void shouldAcceptValidToken() {
        var key = ApiKey.builder()
                .token(VALID_TOKEN)
                .build();
        assertEquals(VALID_TOKEN, key.getToken());
    }

    @ParameterizedTest
    @ValueSource(strings = {"924A8073-8EF2-2046-A7C7-3BC31911DB41C021088A", "???", "API_KEY_test", ""})
    @NullSource
    public void shouldNotAcceptInvalidToken(String token) {
        assertThrows(ApiKeyInvalidException.class, () -> {
            ApiKey.builder()
                    .token(token)
                    .build();
        });
    }

    @Test
    public void shouldAcceptIfPermissionsNotProvided() {
        var key = ApiKey.builder()
                .token(VALID_TOKEN)
                .build();
        assertEquals(ApiPermission.ALL_PERMISSIONS, key.getPermissions());
    }

    @Test
    public void shouldAcceptValidPermissionSet() {
        var permissions = Set.of(ApiPermission.ACCOUNT, ApiPermission.PVP);
        var key = ApiKey.builder()
                .token(VALID_TOKEN)
                .permissions(permissions)
                .build();
        assertEquals(permissions, key.getPermissions());
    }

    @Test
    public void shouldNotAcceptInvalidPermissionSet() {
        assertThrows(ApiKeyInvalidException.class, () -> {
            ApiKey.builder()
                    .token(VALID_TOKEN)
                    .permissions(Set.of(ApiPermission.BUILDS)) //does not have 'ACCOUNT'
                    .build();
        });
    }

}