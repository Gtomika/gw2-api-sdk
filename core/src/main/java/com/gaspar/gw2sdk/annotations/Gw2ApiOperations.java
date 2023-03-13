package com.gaspar.gw2sdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Repeater of {@link Gw2ApiOperation}.
 */
@Target(ElementType.METHOD)
public @interface Gw2ApiOperations {

    Gw2ApiOperation[] value();

}
