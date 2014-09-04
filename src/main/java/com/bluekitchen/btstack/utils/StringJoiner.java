package com.bluekitchen.btstack.utils;

import java.util.Collection;

/**
 * Create a string from a collection of objects with some other string between
 * the elements.
 * @author flacy
 * @param <T> the type of thing that is contained in the collection.
 */
public class StringJoiner<T> {

    private final Converter<T> defaultConverter;

    public StringJoiner() {
        defaultConverter = new Converter<T>() {
            @Override
            public String convert(T thing) {
                return thing.toString();
            }
        };
    }

    /**
     * Create a new string from the given list with <em>joinString</em> between
     * each element of the list, using <em>converter</em> to convert each element to a string.
     * (Which is what the join() method/function of python/ruby does for you.)
     * This handles the excess leading/trailing join string problem for you.
     *
     * @param stuff
     * @param joinString
     * @param converter 
     * @param quote
     * @return
     */
    public String join(Collection<T> stuff, String joinString, Converter<T> converter, boolean quote) {
        StringBuilder sb = new StringBuilder();
        boolean unFirst = false;
        for (T thingy : stuff) {
            if (unFirst) {
                sb.append(joinString).append(condQuote(converter.convert(thingy), quote));
            }
            else {
                sb.append(condQuote(converter.convert(thingy), quote));
                unFirst = true;
            }
        }
        return sb.toString();
    }

    public String join(Collection<T> stuff, String joinString) {
        return join(stuff, joinString, defaultConverter, true);
    }

    public String join(Collection<T> stuff, String joinString, boolean quote) {
        return join(stuff, joinString, defaultConverter, quote);
    }
    
    private String condQuote(String thing, boolean quote) {
        if (quote) {
            StringBuilder sb = new StringBuilder(thing.length() + 2);
            sb.append("\"").append(thing).append("\"");
            return sb.toString();
        }
        else {
            return thing;
        }
    }
}
