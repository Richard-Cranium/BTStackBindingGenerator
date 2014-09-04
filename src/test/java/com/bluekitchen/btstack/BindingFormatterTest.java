package com.bluekitchen.btstack;

import java.io.File;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author flacy
 */
public class BindingFormatterTest {
    
    public BindingFormatterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of format method, of class BindingFormatter.
     * @throws java.lang.Exception
     */
    @Test
    public void testFormat() throws Exception {
        System.out.println("format");
        DefinesParser dp = new DefinesParser();
        EventsParser ep = new EventsParser();
        CommandParser cp = new CommandParser();
        File infile = getResourceFile("hci_cmds.h");
        BindingFormatter instance = new BindingFormatter(infile.getParentFile());
        dp.read(infile);
        ep.read(infile);
        infile = getResourceFile("hci.h");
        dp.read(infile);
        infile = getResourceFile("hci_cmds.c");
        cp.read(infile);
        instance.format(dp, ep, cp);
    }

    private File getResourceFile(String fileName) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(fileName).toURI().getPath());
    }
    
}
