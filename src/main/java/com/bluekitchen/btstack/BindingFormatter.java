package com.bluekitchen.btstack;

import com.bluekitchen.btstack.utils.StringJoiner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.MojoExecutionException;

/**
 *
 * @author flacy
 */
public class BindingFormatter {

    private static final Logger LOG = Logger.getLogger(BindingFormatter.class.getName());

    private static final String TEMPLATE_PROPS = "template-props.xml";

    private static final String PACKAGE = "com.bluekitchen.btstack";
    private static final String EVENT_PACKAGE = PACKAGE + ".event";
    private static final String COMMAND_CLASS = "BTstack";

    private static enum Keys {
        header,
        command,
        footer,
        event_factory,
        event_factory_event,
        event_factory_subevent,
        event,
        event_getter,
        event_getter_data,
        event_to_string,
        define_string,
        event_getter_remaining_data,
        event_handler_header,
        event_handler_handle_decl,
    }

    private final Map<String, String> btStackType2JavaType;
    private final Map<String, Integer> type2size;
    private final Map<Keys, MessageFormat> formatters;
    private final Set<String> definesUsed;
    private final Map<String, MessageFormat> paramStoreMap;
    private final Set<String> specialParamType;
    private final Map<String, MessageFormat> paramReadMap;
    
    private final File outputDirectory;

    public BindingFormatter(File destRoot) throws MojoExecutionException {
        this.outputDirectory = destRoot;
        this.btStackType2JavaType = new HashMap<>();
        this.type2size = new HashMap<>();
        this.formatters = new HashMap<>();
        this.definesUsed = new TreeSet<>();
        this.paramStoreMap = new HashMap<>();
        this.specialParamType = new HashSet<>(2);
        this.paramReadMap = new HashMap<>();
        
        btStackType2JavaType.put("1", "int");
        btStackType2JavaType.put("2", "int");
        btStackType2JavaType.put("3", "int");
        btStackType2JavaType.put("4", "long");
        btStackType2JavaType.put("H", "int");
        btStackType2JavaType.put("B", "BD_ADDR");
        btStackType2JavaType.put("D", "byte []");
        btStackType2JavaType.put("E", "byte []");
        btStackType2JavaType.put("N", "String");
        btStackType2JavaType.put("P", "byte []");
        btStackType2JavaType.put("A", "byte []");
        btStackType2JavaType.put("R", "byte []");
        btStackType2JavaType.put("S", "byte []");
        btStackType2JavaType.put("J", "int");
        btStackType2JavaType.put("L", "int");
        btStackType2JavaType.put("V", "byte []");
        btStackType2JavaType.put("U", "BT_UUID");
        btStackType2JavaType.put("X", "GATTService");
        btStackType2JavaType.put("Y", "GATTCharacteristic");
        btStackType2JavaType.put("Z", "GATTCharacteristicDescriptor");

        type2size.put("1", 1);
        type2size.put("2", 2);
        type2size.put("3", 3);
        type2size.put("4", 4);
        type2size.put("H", 2);
        type2size.put("B", 6);
        type2size.put("D", 8);
        type2size.put("E", 240);
        type2size.put("N", 248);
        type2size.put("P", 16);
        type2size.put("A", 31);
        type2size.put("S", -1);
        type2size.put("V", -1);
        type2size.put("J", 1);
        type2size.put("L", 2);
        type2size.put("U", 16);
        type2size.put("X", 20);
        type2size.put("Y", 24);
        type2size.put("Z", 18);
        
        paramStoreMap.put("1", new MessageFormat("Util.storeByte(command, offset, {0});"));
        paramStoreMap.put("J", new MessageFormat("Util.storeByte(command, offset, {0});"));
        paramStoreMap.put("2", new MessageFormat("Util.storeBt16(command, offset, {0});"));
        paramStoreMap.put("H", new MessageFormat("Util.storeBt16(command, offset, {0});"));
        paramStoreMap.put("L", new MessageFormat("Util.storeBt16(command, offset, {0});"));
        paramStoreMap.put("3", new MessageFormat("Util.storeBt24(command, offset, {0});"));
        paramStoreMap.put("4", new MessageFormat("Util.storeBt32(command, offset, {0});"));
        paramStoreMap.put("D", new MessageFormat("Util.storeBytes(command, offset, {0}, 8);"));
        paramStoreMap.put("E", new MessageFormat("Util.storeBytes(command, offset, {0}, 240);"));
        paramStoreMap.put("P", new MessageFormat("Util.storeBytes(command, offset, {0}, 16);"));
        paramStoreMap.put("A", new MessageFormat("Util.storeBytes(command, offset, {0}, 31);"));
        paramStoreMap.put("S", new MessageFormat("Util.storeBytes(command, offset, {0});"));
        paramStoreMap.put("B", new MessageFormat("Util.storeBytes(command, offset, {0}.getBytes());"));
        paramStoreMap.put("U", new MessageFormat("Util.storeBytes(command, offset, {0}.getBytes());"));
        paramStoreMap.put("X", new MessageFormat("Util.storeBytes(command, offset, {0}.getBytes());"));
        paramStoreMap.put("Y", new MessageFormat("Util.storeBytes(command, offset, {0}.getBytes());"));
        paramStoreMap.put("Z", new MessageFormat("Util.storeBytes(command, offset, {0}.getBytes());"));
        paramStoreMap.put("N", new MessageFormat("Util.storeString(command, offset, {0}, 248);"));
        
        specialParamType.add("L");
        specialParamType.add("J");
        
        paramReadMap.put("1", new MessageFormat("return Util.readByte(data, {0});"));
        paramReadMap.put("J", new MessageFormat("return Util.readByte(data, {0});"));
        paramReadMap.put("2", new MessageFormat("return Util.readBt16(data, {0});"));
        paramReadMap.put("3", new MessageFormat("return Util.readBt24(data, {0});"));
        paramReadMap.put("4", new MessageFormat("return Util.readBt32(data, {0});"));
        paramReadMap.put("H", new MessageFormat("return Util.readBt16(data, {0});"));
        paramReadMap.put("L", new MessageFormat("return Util.readByte(data, {0});"));
        paramReadMap.put("B", new MessageFormat("return Util.readBdAddr(data, {0});"));
        paramReadMap.put("X", new MessageFormat("return Util.readGattService(data, {0});"));
        paramReadMap.put("Y", new MessageFormat("return Util.readGattCharacteristic(data, {0});"));
        paramReadMap.put("Z", new MessageFormat("return Util.readGattCharacteristicDescriptor(data, {0});"));
        
        getFormatters();
    }

    public void format(DefinesParser dp, EventsParser ep, CommandParser cp) throws MojoExecutionException {
        try {
            File stackFile = createFile(PACKAGE, COMMAND_CLASS);
            try ( FileWriter writer = new FileWriter(stackFile)) {
                writeToWriter(writer, formatters.get(Keys.header), PACKAGE, COMMAND_CLASS);
                for (CommandInfo ci : cp) {
                    format(writer, ci);
                }
                writeToWriter(writer, "\n    /** defines used */\n\n");
                for (String defKey : definesUsed) {
                    writeToWriter(writer, formatters.get(Keys.define_string), defKey, dp.getValue(defKey));
                }
                writeToWriter(writer, formatters.get(Keys.footer).toPattern());
            }
            format(ep, dp);
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to create java output file(s).", ex);
        }
    }

    private void format(FileWriter writer, CommandInfo ci) throws IOException {
        LOG.log(Level.INFO, "Formatting {0}", ci);
        final String indent = "        ";
        MessageFormat mf = new MessageFormat("{0} {1}");
        List<String> args2 = new ArrayList<>(ci.getFormat().length());
        int sizeFixed = 3;
        StringBuilder sizeVar = new StringBuilder();
        StringBuilder storeParams = new StringBuilder();
        String lengthName = "";
        int maxItems = Math.min(ci.getFormat().length(), ci.getParms().size());
        for (int i = 0; i < maxItems; i++) {
            String paramType = ci.getFormat().substring(i, i+1);
            String argName = ci.getParms().get(i);
            String argType = btStackType2JavaType.get(paramType);
            args2.add(mf.format(new Object[] { argType, argName }));
            LOG.log(Level.FINE, "index: {0} char: {1} arg: {2}", new Object[]{i, paramType, argName});
            Integer argSize = type2size.get(paramType);
            if (argSize > 0) {
                sizeFixed += argSize;
            } else {
                sizeVar.append(" + ").append(argName).append(".length");
            }

            if (specialParamType.contains(paramType)) {
                lengthName = argName;
            }
            if (paramType.equals("V")) {
                storeParams.append(MessageFormat.format("{0}Util.storeBytes(command, offset, {1}, {2});\n", indent, argName, lengthName));
                storeParams.append(MessageFormat.format("{0}offset += {1};\n", indent, lengthName));
                lengthName = "";
            } else {
                storeParams.append(indent).append(paramStoreMap.get(paramType).format(new Object[] {argName})).append("\n");
                if (argSize > 0) {
                    storeParams.append(MessageFormat.format("{0}offset += {1};\n", indent, argSize));
                } else {
                    storeParams.append(MessageFormat.format("{0}offset += {1}.length;\n", indent, argName));
                }
            }
        
        }
        StringJoiner<String>joiner = new StringJoiner<>();
        String argsString = joiner.join(args2, ", ",false);
        String sizeString = MessageFormat.format("{0}{1}", sizeFixed, sizeVar);
        writeToWriter(writer, formatters.get(Keys.command), ci.getName(), argsString, ci.getFormat(), sizeString, ci.getOgf(), ci.getOcf(), storeParams);
        addDefineUsed(ci.getOgf());
        addDefineUsed(ci.getOcf());
    }
    
    private void format(EventsParser ep, DefinesParser dp) throws IOException {
        // Normal events first
        for (EventInfo ei : ep.getEventIterable()) {
            format(ei);
        }
        // LE events next
        for (EventInfo ei : ep.getLeEventIterable()) {
            format(ei);
        }
        // Create the event factory
        File outfile = createFile(PACKAGE, "EventFactory");
        try (FileWriter writer = new FileWriter(outfile)) {
            StringBuilder cases = new StringBuilder();
            for (EventInfo ei : ep.getEventIterable()) {
                cases.append(formatters.get(Keys.event_factory_event).format(new Object[]{ei.getValue(), NameUtilities.getInstance().camelCase(ei.getKey())}));
            }
            StringBuilder subcases = new StringBuilder();
            for (EventInfo lei : ep.getLeEventIterable()) {
                subcases.append(formatters.get(Keys.event_factory_subevent).format(new Object[]{lei.getValue(), NameUtilities.getInstance().camelCase(lei.getKey())}));
            }
            
            MessageFormat dff = formatters.get(Keys.define_string);
            List<String> stuff = new ArrayList<>();
            for (String etd : ep.getEventTypes()) {
                stuff.add(dff.format(new Object[]{etd, dp.getValue(etd)}));
            }
            StringJoiner<String> joiner = new StringJoiner<>();
            String definesText = joiner.join(stuff, "\n", false);
            writeToWriter(writer, formatters.get(Keys.event_factory), PACKAGE, definesText, cases, subcases);
        }
        // Create the EventHandler interface.
        File handlerFile = createFile(EVENT_PACKAGE, "EventHandler");
        try (FileWriter writer = new FileWriter(handlerFile)) {
            writeToWriter(writer, formatters.get(Keys.event_handler_header), EVENT_PACKAGE, PACKAGE);
            for (EventInfo ei : ep.getEventIterable()) {
                writeToWriter(writer, formatters.get(Keys.event_handler_handle_decl), NameUtilities.getInstance().camelCase(ei.getKey()));
            }
            for (EventInfo lei : ep.getLeEventIterable()) {
                writeToWriter(writer, formatters.get(Keys.event_handler_handle_decl), NameUtilities.getInstance().camelCase(lei.getKey()));
            }
            writeToWriter(writer, formatters.get(Keys.footer).toPattern());
        }
    }
    
    private void format(EventInfo ei) throws IOException {
        LOG.log(Level.INFO, "Formatting {0}", ei);
        String eventName = NameUtilities.getInstance().camelCase(ei.getKey());
        LOG.log(Level.FINE, "eventName is {0}", eventName);
        File outfile = createFile(EVENT_PACKAGE, eventName);
        try (FileWriter writer = new FileWriter(outfile)) {
            int offset = 2;
            int size;
            StringBuilder getters = new StringBuilder();
            String lengthName = "";
            String access;
            int itemsLength = Math.min(ei.getFormat().length(), ei.getParams().size());
            for (int i = 0; i < itemsLength; i++) {
                String f = ei.getFormat().substring(i, i+1);
                String arg = ei.getParams().get(i);
                if (specialParamType.contains(f)) {
                    lengthName = NameUtilities.getInstance().camelCase(arg);
                }
                switch (f) {
                    case "V":
                        access = formatters.get(Keys.event_getter_data).format(new Object[] {lengthName, offset});
                        size = 0;
                        break;
                    case "R":
                        access = formatters.get(Keys.event_getter_remaining_data).format(new Object[] {offset});
                        size = 0;
                        break;
                    default:
                        access = paramReadMap.get(f).format(new Object[] {offset});
                        size = type2size.get(f);
                }
                getters.append(formatters.get(Keys.event_getter).format(new Object[]{btStackType2JavaType.get(f), NameUtilities.getInstance().camelCase(arg), access}));
                offset += size;
            }
            StringBuilder toStringArgs = new StringBuilder();
            final String indent = "        ";
            for (String arg : ei.getParams()) {
                toStringArgs.append(indent).append("t.append(\", ").append(arg).append(" = \");\n");
                toStringArgs.append(indent).append("t.append(get").append(NameUtilities.getInstance().camelCase(arg)).append("());\n");
            }
            String toStringMethod = formatters.get(Keys.event_to_string).format(new Object[] {eventName, toStringArgs});
            writeToWriter(writer, formatters.get(Keys.event), PACKAGE, eventName, getters, toStringMethod);
        }
    }
    
    
    private void writeToWriter(FileWriter writer, MessageFormat mf, Object... args) throws IOException {
        writeToWriter(writer, mf.format(args));
    }
    
    private void writeToWriter(FileWriter writer, String stuff) throws IOException {
        writer.write(stuff);
    }
    
    private void addDefineUsed(String def) {
        if (def.startsWith("0")) {
            return;
        }
        definesUsed.add(def);
    }
    
    private File createFile(String packageName, String className) throws IOException {

        String directoryPath = packageName.replace('.', '/');

        File packageDirs = new File(outputDirectory, directoryPath);
        packageDirs.mkdirs();
        String classFilePath = directoryPath + "/" + className + ".java";
        LOG.log(Level.INFO, "Creating file: {0} in directory: {1}", new Object[]{classFilePath, outputDirectory});
        File aClassFile = new File(outputDirectory, classFilePath);
        aClassFile.createNewFile();
        return aClassFile;
    }

    private void getFormatters() throws MojoExecutionException {
        Properties formatTemplates = new Properties();
        try (InputStream is = BindingFormatter.class.getResourceAsStream(TEMPLATE_PROPS)) {
            formatTemplates.loadFromXML(is);
        } catch (Exception ex) {
            throw new MojoExecutionException("Unable to read formatter property file.", ex);
        }
        // Do a sanity check to make sure that every member of the enum has a key in the file
        // and that every key in the file has a enum to match.
        // A enum with no matching key is an error.  A key with no enum indicates that you forgot
        // to remove an unused key from the property file and worthy of a warning.
        Set<String> ekeys = new HashSet<>();
        Set<String> pfkeys = new HashSet<>(formatTemplates.stringPropertyNames());
        for (Keys key : Keys.values()) {
            LOG.log(Level.INFO, "Processing key {0}", key);
            ekeys.add(key.toString());
            String prop = formatTemplates.getProperty(key.toString());
            if (prop != null) {
                formatters.put(key, new MessageFormat(prop));
            }
        }
        if (ekeys.containsAll(formatTemplates.stringPropertyNames()) && formatTemplates.stringPropertyNames().containsAll(ekeys)) {
            LOG.info("Template properties file keys match enumeration.");
        } else {
            Set<String> tpk = new TreeSet<>(formatTemplates.stringPropertyNames());
            tpk.removeAll(ekeys);
            if (!tpk.isEmpty()) {
                LOG.warning("The following properties file keys do not have matching enumeration keys:");
                for (String badk : tpk) {
                    LOG.log(Level.WARNING, "property file key: {0}", badk);
                }
            }
            Set<String> tek = new TreeSet<>(ekeys);
            tek.removeAll(formatTemplates.stringPropertyNames());
            if (!tek.isEmpty()) {
                LOG.severe("The following enumeration keys do not have matching keys in the properties file:");
                for (String badk : tek) {
                    LOG.log(Level.SEVERE, "enumeration key: {0}", badk);
                }
                // You'll get a NPE later if all the keys are in use, so fail now.
                throw new MojoExecutionException("The format properties xml file is missing required entries.\nCheck the logs for a listing.");
            }
        }
        
    }
}
