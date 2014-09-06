package com.bluekitchen.btstack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author flacy
 */
public class NameUtilities {

    private final Set<String> acronyms;
    private final Set<String> uuidNames;

    private NameUtilities() {
        acronyms = new HashSet<>();
        String[] acro = new String[]{
            "GAP",
            "GATT",
            "HCI",
            "L2CAP",
            "LE",
            "RFCOMM",
            "SM",
            "SDP",
            "UUID16",
            "UUID128",};

        acronyms.addAll(Arrays.asList(acro));

        uuidNames = new HashSet<>();
        uuidNames.add("uuid128");
        uuidNames.add("uuid16");

    }

    String cap(String in) {
        if (in.equalsIgnoreCase("btstack")) {
            return "BTstack";
        }
        if (acronyms.contains(in.toUpperCase())) {
            return in.toUpperCase();
        }
        return capitalize(in);
    }

    String camelCase(String name) {
        String[] split = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String thingy : split) {
            sb.append(cap(thingy));
        }
        return sb.toString();
    }

    String camelCaseVar(String name) {
        if (uuidNames.contains(name)) {
            return name;
        }
        return lcFirst(camelCase(name));
    }

    public String quoteStr(String toQuote) {
        StringBuilder sb = new StringBuilder(toQuote.length() + 4);
        sb.append("\"");
        for (int i = 0; i < toQuote.length(); i++) {
            char charAt = toQuote.charAt(i);
            switch (charAt) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    sb.append(charAt);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    /**
     * Uppercase the first letter of the string and lowercase the rest.
     *
     * @param inName
     * @return
     */
    public String capitalize(String inName) {
        if (inName == null) {
            return inName;
        }
        switch (inName.length()) {
            case 0:
                return inName;
            case 1:
                return inName.toUpperCase();
            default:
                return Character.toUpperCase(inName.charAt(0)) + inName.substring(1).toLowerCase();
        }
    }

    /**
     * Lowercase the first letter of the string and leave the others alone.
     *
     * @param inName
     * @return
     */
    public String lcFirst(String inName) {
        if (inName == null) {
            return inName;
        }
        switch (inName.length()) {
            case 0:
                return inName;
            case 1:
                return inName.toLowerCase();
            default:
                return Character.toLowerCase(inName.charAt(0)) + inName.substring(1);
        }
    }

    /**
     * Uppercase the first letter of the string and leave the others alone.
     *
     * @param inName
     * @return
     */
    public String ucFirst(String inName) {
        if (inName == null) {
            return inName;
        }
        switch (inName.length()) {
            case 0:
                return inName;
            case 1:
                return inName.toUpperCase();
            default:
                return Character.toUpperCase(inName.charAt(0)) + inName.substring(1);
        }
    }

    public static NameUtilities getInstance() {
        return NameUtilitiesHolder.INSTANCE;
    }

    private static class NameUtilitiesHolder {

        private static final NameUtilities INSTANCE = new NameUtilities();

        private NameUtilitiesHolder() {
        }
    }
}
