package com.gaspar.gw2sdk.auth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * All permissions that can be granted to an API key.
 */
public enum Gw2Permission {
    ACCOUNT,
    INVENTORIES,
    CHARACTERS,
    TRADING_POST,
    WALLET,
    UNLOCKS,
    PVP,
    BUILDS,
    PROGRESSION,
    GUILDS;

    /**
     * Set with all permissions that a Gw2 API key can have.
     */
    public static final Set<Gw2Permission> ALL_PERMISSIONS;

    /**
     * Set with minimal permissions that a Gw2 API key must have.
     */
    public static final Set<Gw2Permission> MINIMAL_PERMISSIONS;

    static {
        ALL_PERMISSIONS = new HashSet<>();
        ALL_PERMISSIONS.addAll(Arrays.asList(Gw2Permission.values()));

        MINIMAL_PERMISSIONS = Set.of(ACCOUNT);
    }
}
