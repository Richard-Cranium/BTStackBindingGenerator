package com.bluekitchen.btstack;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author flacy
 */
public class CommandParserTest {
    
    public CommandParserTest() {
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
     * Test of read method, of class CommandParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        File infile = new File(getClass().getClassLoader().getResource("hci_cmds.c").toURI().getPath());
        CommandParser instance = new CommandParser();
        instance.read(infile);
        for (CommandInfo ci : instance) {
            System.out.println(ci.toString());
        }
    }
    
}
