package com.pg85.otg.util;

import lombok.Getter;

/**
 * A dummy class to allow for debugging of values when debugging mixins
 * @param <T>
 */
@Getter
public class CrashDummy<T> {

    private final T value;

    public CrashDummy(T value) {
        this.value = value;
        System.out.println("CrashDummy created with value: " + value);
    }

}
