package com.gaspar.gw2sdk;

import java.util.List;

public class Gw2InvalidParamException extends Gw2SdkException {

    public Gw2InvalidParamException(String paramName, Object invalidValue, List<String> reasons) {
        super(String.format("The parameter '{}' has invalid value '{}'. Reasons: {}", paramName, invalidValue, reasons));
    }
}
