package com.github.vincentrussell.ini;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class IniParserTest {

    @Test
    public void testNumbers() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Double.valueOf(3.14), iniParser.getValue("Numbers", "double"));
        assertEquals(Double.valueOf(3.14D), iniParser.getValue("Numbers", "double2"));
        assertEquals(Double.parseDouble("199.33F"), iniParser.getValue("Numbers", "float"));
        assertEquals(Long.valueOf(404), iniParser.getValue("Numbers", "integer"));
        assertEquals(922337203685775808L, iniParser.getValue("Numbers", "long"));
        assertEquals(922337203685775808L, iniParser.getValue("Numbers", "long2"));
        assertEquals(Long.valueOf(-32768), iniParser.getValue("Numbers", "short"));
    }


    @Test
    public void getStrings() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals("Hello", iniParser.getValue("String", "string"));
        assertEquals("Henry", iniParser.getValue("String", "user"));
    }

    @Test
    public void sectionNotFound() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertNull(iniParser.getValue("notFound", "notFound"));
        assertNull(iniParser.getValue("Numbers", "notFound"));
    }

    @Test
    public void getSections() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertThat(iniParser.getSections(), containsInAnyOrder("FTPS", "FTP", "HTTPS", "BACKUP_SERVERS", "SNMP", "TFTP", "HTTP", "Settings"));
    }

    @Test
    public void getKeys() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertThat(iniParser.getKeys("FTPS"), containsInAnyOrder("RunFTPS", "FTPPort", "FTPDataPort"));
        assertTrue(iniParser.getKeys("notFound").isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void nullInputStream() throws IOException {
        new IniParser(null);
    }
}