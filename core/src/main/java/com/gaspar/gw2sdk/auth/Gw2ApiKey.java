package com.gaspar.gw2sdk.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Stores a GW2 API key token and all related information.
 */
@Slf4j
@Builder
public class Gw2ApiKey {

    private static final Pattern GW2_API_KEY_PATTERN = Pattern.compile(
        "[A-F0-9]{8}-([A-F0-9]{4}-){3}[A-F0-9]{20}-([A-F0-9]{4}-){3}[A-F0-9]{12}"
    );

    /**
     * The API key token. It will be validated to have the expected format.
     */
    @Getter
    private final String token;

    /**
     * Set of permissions that belong to this API key. This is not mandatory, but recommended being provided.
     * <li>
     *     <ul>
     *         If provided, the SDK can perform permission checks before making HTTP calls. The permissions
     *         must contain at least the {@link Gw2Permission#MINIMAL_PERMISSIONS}.
     *     </ul>
     *     <ul>
     *         If not provided, it will default to {@link Gw2Permission#ALL_PERMISSIONS}, but this can be an
     *         issue if the API key really does not have all permissions.
     *     </ul>
     * </li>
     */
    @Getter
    private final Set<Gw2Permission> permissions;

    private Gw2ApiKey(String token, Set<Gw2Permission> permissions) {
        this.token = validateToken(token);
        this.permissions = permissions != null ? validatePermissions(permissions) : Gw2Permission.ALL_PERMISSIONS;
        log.debug("Created API key. Token: {}, Permissions: {}", this.token, this.permissions);
    }

    private String validateToken(String token) throws Gw2ApiKeyInvalidException {
        if(StringUtils.isBlank(token)) {
            throw new Gw2ApiKeyInvalidException(token, "The token cannot be null or empty");
        }
        if(!GW2_API_KEY_PATTERN.matcher(token).matches()) {
            throw new Gw2ApiKeyInvalidException(token, "The token does not match the expected format");
        }
        return token;
    }

    private Set<Gw2Permission> validatePermissions(Set<Gw2Permission> permissions) throws Gw2ApiKeyInvalidException {
        if(!permissions.containsAll(Gw2Permission.MINIMAL_PERMISSIONS)) {
            throw new Gw2ApiKeyInvalidException("The permissions set does not contain at least the minimal permissions." +
                    " See 'Gw2Permission.MINIMAL_PERMISSIONS'.");
        }
        return permissions;
    }
}
