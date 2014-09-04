package com.bluekitchen.btstack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author flacy
 */
public class EventsParser {

    private static final String FORMAT_PATTERN = ".*@format\\s*(\\w*)\\s*";
    private static final String PARM_PATTERN = ".*@param\\s*(\\w*)\\s*";
    private static final String PARTS_PATTERN = "#define\\s+(\\w+)\\s+(\\w*).*";

    private final Pattern format;
    private final Pattern parm;
    private final Pattern defines;
    private final List<EventInfo> events;
    private final List<EventInfo> leEvents;
    private final Set<String> eventTypes;
    private final Charset charset;
    private static final Logger LOG = Logger.getLogger(EventsParser.class.getName());

    public EventsParser() {
        this.charset = Charset.defaultCharset();
        this.format = Pattern.compile(FORMAT_PATTERN);
        this.parm = Pattern.compile(PARM_PATTERN);
        this.defines = Pattern.compile(PARTS_PATTERN);
        this.events = new ArrayList<>();
        this.leEvents = new ArrayList<>();
        this.eventTypes = new TreeSet<>();
    }

    public void read(final File infile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(infile.toURI()), charset)) {
            String line;
            String formats = null;
            List<String> parms = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                Matcher fm = format.matcher(line);
                if (fm.matches()) {
                    if (fm.groupCount() == 1) {
                        formats = fm.group(1);
                    }
                }
                Matcher pm = parm.matcher(line);
                if (pm.matches()) {
                    if (pm.groupCount() == 1) {
                        parms.add(pm.group(1));
                    }
                }
                Matcher dm = defines.matcher(line);
                if (dm.matches()) {
                    if (dm.groupCount() == 2) {
                        String key = dm.group(1);
                        String value = dm.group(2);
                        if (formats != null) {
                            if (key.toLowerCase().startsWith("hci_subevent_")) {
                                leEvents.add(new EventInfo(value, key.toLowerCase().replace("hci_subevent_", "hci_event_"), formats, parms));
                            } else {
                                events.add(new EventInfo(value, key, formats, parms));
                            }
                            eventTypes.add(key);
                        } else {
                            LOG.log(Level.FINE, "Found definition without format! {0}", line);
                        }
                        parms.clear();
                        formats = null;
                    }
                }
            }

        }
    }
    
    public Iterable<EventInfo> getEventIterable() {
        return events;
    }
    
    public Iterable<EventInfo> getLeEventIterable() {
        return leEvents;
    }
    
    public String[] getEventTypes() {
        return eventTypes.toArray(new String[eventTypes.size()]);
    }
}
