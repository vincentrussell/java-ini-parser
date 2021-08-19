package com.github.vincentrussell.ini;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class IniTest {

    @Test
    public void testNumbers() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Double.valueOf(3.14), ini.getValue("Numbers", "double"));
        assertEquals(Double.valueOf(3.14D), ini.getValue("Numbers", "double2"));
        assertEquals(Double.parseDouble("199.33F"), ini.getValue("Numbers", "float"));
        assertEquals(Long.valueOf(404), ini.getValue("Numbers", "integer"));
        assertEquals(922337203685775808L, ini.getValue("Numbers", "long"));
        assertEquals(922337203685775808L, ini.getValue("Numbers", "long2"));
        assertEquals(Long.valueOf(-32768), ini.getValue("Numbers", "short"));
    }

    @Test
    public void getAndSpecifyType() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Double.valueOf(3.14), ini.getValue("Numbers", "double", Double.class));
        assertEquals(Double.valueOf(3.14), ini.getValue("Numbers", "double", double.class));
    }

    @Test
    public void getAndSpecifyTypeConvertNumber() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals(Integer.valueOf(-32768), ini.getValue("Numbers", "short", int.class));
        assertEquals(Double.valueOf(-32768), ini.getValue("Numbers", "short", double.class));
        assertEquals(Float.valueOf(-32768), ini.getValue("Numbers", "short", float.class));
    }

    @Test
    public void getAndSpecifyTypeConvertNumber2() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        //loss of precision
        assertEquals(Integer.valueOf(-858695232), ini.getValue("Numbers", "long", int.class));
    }

    @Test
    public void getNumbersAsString() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals("3.14", ini.getValue("Numbers", "double", String.class));
        assertEquals("3.14", ini.getValue("Numbers", "double2", String.class));
        assertEquals("199.33", ini.getValue("Numbers", "float", String.class));
        assertEquals("404", ini.getValue("Numbers", "integer", String.class));
        assertEquals("922337203685775808", ini.getValue("Numbers", "long", String.class));
        assertEquals("922337203685775808", ini.getValue("Numbers", "long2", String.class));
        assertEquals("-32768", ini.getValue("Numbers", "short", String.class));
    }


    @Test
    public void getStrings() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertEquals("Hello", ini.getValue("String", "string"));
        assertEquals("Henry", ini.getValue("String", "user"));
    }

    @Test
    public void putValues() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        ini.putValue("String", "newString", "Hello2");
        ini.putValue("Numbers", "double2", Double.valueOf("3.1455"));
        assertEquals("Hello2", ini.getValue("String", "newString"));
        assertEquals(Double.valueOf("3.1455"), ini.getValue("Numbers", "double2"));
    }

    @Test
    public void sectionNotFound() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample.ini"));
        assertNull(ini.getValue("notFound", "notFound"));
        assertNull(ini.getValue("Numbers", "notFound"));
    }

    @Test
    public void getSections() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertThat(ini.getSections(), containsInAnyOrder("FTPS", "FTP", "HTTPS", "BACKUP_SERVERS", "SNMP", "TFTP", "HTTP", "Settings"));
    }

    @Test
    public void getSectionAsMap() throws IOException {
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertEquals(ini.getSection("FTP"), ImmutableMap.<String, Object>builder()
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
        Ini ini = new Ini();
        ini.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/sample2.ini"));
        assertThat(ini.getKeys("FTPS"), containsInAnyOrder("RunFTPS", "FTPPort", "FTPDataPort"));
        assertTrue(ini.getKeys("notFound").isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void nullInputStream() throws IOException {
        Ini ini = new Ini();
        ini.load((InputStream) null);
    }
}