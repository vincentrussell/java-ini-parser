package com.github.vincentrussell.ini;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
    public void getAndSpecifyType() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Double.valueOf(3.14), iniParser.getValue("Numbers", "double", Double.class));
        assertEquals(Double.valueOf(3.14), iniParser.getValue("Numbers", "double", double.class));
    }

    @Test
    public void getAndSpecifyTypeConvertNumber() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Integer.valueOf(-32768), iniParser.getValue("Numbers", "short", int.class));
        assertEquals(Double.valueOf(-32768), iniParser.getValue("Numbers", "short", double.class));
        assertEquals(Float.valueOf(-32768), iniParser.getValue("Numbers", "short", float.class));
    }

    @Test
    public void getAndSpecifyTypeConvertNumber2() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        //loss of precision
        assertEquals(Integer.valueOf(-858695232), iniParser.getValue("Numbers", "long", int.class));
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
    public void getSectionAsMap() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertEquals(iniParser.getSection("FTP"), ImmutableMap.<String, Object>builder()
                .put("FTPDir", "/opt/ecs/mvuser/MV_IPTel/data/FTPdata")
                .put("SUUserName", "mvuser")
                .put("SUPassword", "Avaya")
                .put("RunFTP", 1L)
                .put("EnableSU", 1L)
                .put("FTPPort", 21L)
                .put("FTPDataPort", 20L)
                .put("FTP_TimeOut", 5L)
                .build());
    }

    @Test
    public void getKeys() throws IOException {
        IniParser iniParser = new IniParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertThat(iniParser.getKeys("FTPS"), containsInAnyOrder("RunFTPS", "FTPPort", "FTPDataPort"));
        assertTrue(iniParser.getKeys("notFound").isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void nullInputStream() throws IOException {
        new IniParser((InputStream) null);
    }
}