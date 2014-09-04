package com.bluekitchen.btstack;

import java.io.File;
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
public class DefinesParserTest {
    
    public DefinesParserTest() {
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
     * Test of read method, of class DefinesParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        File infile = new File(getClass().getClassLoader().getResource("hci.h").toURI().getPath());
        DefinesParser instance = new DefinesParser();
        instance.read(infile);
        for (String key : instance) {
            System.out.println("Definition[" + key + "] = " + instance.getValue(key) );
        }
    }
    
}
