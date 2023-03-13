package com.gaspar.gw2sdk;

import java.util.List;

/**
 * Exception thrown by the SDK if one provided parameter has invalid value.
 */
public class InvalidParamException extends SdkException {

    public InvalidParamException(String paramName, Object invalidValue, List<String> reasons) {
        super(String.format("The parameter '{}' has invalid value '{}'. Reasons: {}", paramName, invalidValue, reasons));
    }
}
