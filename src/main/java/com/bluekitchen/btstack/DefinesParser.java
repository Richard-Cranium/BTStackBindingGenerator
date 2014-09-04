package com.bluekitchen.btstack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author flacy
 */
public class DefinesParser implements Iterable<String> {

    private static final String DEF_PATTERN = "#define\\s+(\\w+)\\s+(\\w*)";

    private final Pattern defPattern;
    private final Map<String,String> defines;
    private final Charset charset;
    
    public DefinesParser() {
        this.defPattern = Pattern.compile(DEF_PATTERN);
        this.defines = new HashMap<>();
        this.charset = Charset.defaultCharset();
    }

    public final void read(final File infile) throws IOException {
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(infile.toURI()),charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = defPattern.matcher(line);
                if (matcher.matches()) {
                    if (matcher.groupCount() == 2) {
                        defines.put(matcher.group(1), matcher.group(2));
                    }
                }
            }
        }
    }

    @Override
    public final Iterator<String> iterator() {
        return defines.keySet().iterator();
    }
    
    public final String getValue(String key) {
        return defines.get(key);
    }
    
}
