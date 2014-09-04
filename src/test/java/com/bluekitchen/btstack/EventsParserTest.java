package com.bluekitchen.btstack;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
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
public class EventsParserTest {

    public EventsParserTest() {
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
     * Test of read method, of class EventsParser.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        File infile = new File(getClass().getClassLoader().getResource("hci_cmds.h").toURI().getPath());
        EventsParser instance = new EventsParser();
        instance.read(infile);
    }

    /**
     * Test of getEventIterable method, of class EventsParser.
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    @Test
    public void testGetEventIterable() throws URISyntaxException, IOException {
        System.out.println("getEventIterator");
        File infile = new File(getClass().getClassLoader().getResource("hci_cmds.h").toURI().getPath());
        EventsParser instance = new EventsParser();
        instance.read(infile);
        for (EventInfo eventInfo : instance.getEventIterable()) {
            System.out.println(eventInfo.toString());
        }
    }

    /**
     * Test of getLeEventIterable method, of class EventsParser.
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testGetLeEventIterable() throws IOException, URISyntaxException {
        System.out.println("getLeEventIterator");
        File infile = new File(getClass().getClassLoader().getResource("hci_cmds.h").toURI().getPath());
        EventsParser instance = new EventsParser();
        instance.read(infile);
        for (EventInfo ei : instance.getLeEventIterable()) {
            System.out.println(ei.toString());
        }
    }

    /**
     * Test of getEventTypes method, of class EventsParser.
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    @Test
    public void testGetEventTypes() throws URISyntaxException, IOException {
        System.out.println("getEventTypes");
        File infile = new File(getClass().getClassLoader().getResource("hci_cmds.h").toURI().getPath());
        EventsParser instance = new EventsParser();
        instance.read(infile);
        for (String et : instance.getEventTypes()) {
            System.out.println(et);
        }
    }

}
