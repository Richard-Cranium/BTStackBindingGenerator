package com.bluekitchen.btstack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author flacy
 */
public class CommandParser implements Iterable<CommandInfo> {

    private static final String PARAM_PATTERN = ".*@param\\s*(\\w*)\\s*";
    private static final String DECL_PATTERN = "const\\s+hci_cmd_t\\s+(\\w+)\\s*=.*";
    private static final String DEF_OPCODE_PATTERN = "\\s*OPCODE\\(\\s*(\\w+)\\s*,\\s+(\\w+)\\s*\\)\\s*,\\s\\\"(\\w*)\\\".*";

    private final Pattern paramPattern;
    private final Pattern declPattern;
    private final Pattern opcodePattern;
    private final Charset charset;
    
    private final List<CommandInfo> commands;
    
    public CommandParser() {
        this.paramPattern = Pattern.compile(PARAM_PATTERN);
        this.declPattern = Pattern.compile(DECL_PATTERN);
        this.opcodePattern = Pattern.compile(DEF_OPCODE_PATTERN);
        this.charset = Charset.defaultCharset();
        this.commands = new ArrayList<>();
    }

    public void read(final File infile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(infile.toURI()),charset)) {
            String line;
            ArrayList<String> parms = new ArrayList<>();
            String cmdName = "";
            while ((line = reader.readLine()) != null) {
                Matcher pm = paramPattern.matcher(line);
                if (pm.matches()) {
                    if (pm.groupCount() == 1) {
                        parms.add(NameUtilities.getInstance().camelCaseVar(pm.group(1)));
                        continue;
                    }
                }
                Matcher declm = declPattern.matcher(line);
                if (declm.matches()) {
                    cmdName = NameUtilities.getInstance().camelCase(declm.group(1));
                    if (cmdName.endsWith("Cmd")) {
                        cmdName = cmdName.substring(0,cmdName.lastIndexOf("Cmd"));
                    }
                    continue;
                }
                Matcher opm = opcodePattern.matcher(line);
                if (opm.matches()) {
                    String format = opm.group(3);
                    if (format.length() != parms.size()) {
                        // well, format length wins in the python code.
                        parms.clear();
                        for (int i = 0; i < format.length(); i++) {
                            parms.add(MessageFormat.format("arg{0}", i+1));
                        }
                    }
                    CommandInfo info = new CommandInfo(parms, cmdName, opm.group(1), opm.group(2), opm.group(3));
                    commands.add(info);
                    parms.clear();
                }
            }
        }
    }

    @Override
    public Iterator<CommandInfo> iterator() {
        return commands.iterator();
    }

}
