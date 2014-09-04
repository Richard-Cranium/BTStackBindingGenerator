package com.bluekitchen.btstack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author flacy
 */
public class EventInfo {
    private final String value;
    private final String key;
    private final String format;
    private final List<String> params;

    public EventInfo(String value, String key, String format, List<String> params) {
        this.value = value;
        this.key = key;
        this.format = format;
        this.params = new ArrayList<>(params);
    }

    @Override
    public String toString() {
        return "EventInfo{" + "value=" + value + ", key=" + key + ", format=" + format + ", params=" + params + '}';
    }

    /**
     * @return the value
     */
    String getValue() {
        return value;
    }

    /**
     * @return the key
     */
    String getKey() {
        return key;
    }

    /**
     * @return the format
     */
    String getFormat() {
        return format;
    }

    /**
     * @return the params
     */
    List<String> getParams() {
        return params;
    }
    
    
    
}
