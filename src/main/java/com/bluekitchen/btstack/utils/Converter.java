package com.bluekitchen.btstack.utils;

/**
 * Convert something into a String.
 * @author flacy
 * @param <T> the type of thing that you are converting.
 */
public interface Converter<T> {
    public String convert(T thing);
}
