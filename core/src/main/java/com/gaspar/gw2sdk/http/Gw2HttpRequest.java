package com.gaspar.gw2sdk.http;

import java.net.URI;
import java.util.Optional;

public record Gw2HttpRequest(
        URI url,
        Optional<String> apiKey,
        String schemaVersion
) {
}
