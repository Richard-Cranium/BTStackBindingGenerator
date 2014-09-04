package com.bluekitchen.btstack;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author flacy
 */
public class CommandInfo {
    private final List<String> parms;
    private final String name;
    private final String ogf;
    private final String ocf;
    private final String format;

    public CommandInfo(List<String> parms, String name, String ogf, String ocf, String format) {
        this.parms = new ArrayList<>(parms);
        this.name = name;
        this.ogf = ogf;
        this.ocf = ocf;
        this.format = format;
        fixParms();
    }
    
    private void fixParms() {
        if (getParms().size() != getFormat().length()) {
            MessageFormat argname = new MessageFormat("arg{0}");
            getParms().clear();
            for (int i = 0; i < getFormat().length(); i++) {
                getParms().add(argname.format(new Object[] {i}));
            }
        }
    }

    @Override
    public String toString() {
        return "CommandInfo{" + "parms=" + getParms() + ", name=" + getName() + ", ogf=" + getOgf() + ", ocf=" + getOcf() + ", format=" + getFormat() + '}';
    }

    /**
     * @return the parms
     */
    List<String> getParms() {
        return parms;
    }

    /**
     * @return the name
     */
    String getName() {
        return name;
    }

    /**
     * @return the ogf
     */
    String getOgf() {
        return ogf;
    }

    /**
     * @return the ocf
     */
    String getOcf() {
        return ocf;
    }

    /**
     * @return the format
     */
    String getFormat() {
        return format;
    }
    
    
}
