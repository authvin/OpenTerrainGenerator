package com.pg85.otg.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@Setting
public @interface StringListSetting {
    String[] DEFAULT_VALUE = {};
    String[] value() default {};
}
