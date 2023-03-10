package com.gaspar.gw2sdk;

/**
 * Data about an error response from the API.
 */
public record Gw2ApiErrorData(
   String errorMessage,
   int statusCode
) {}
