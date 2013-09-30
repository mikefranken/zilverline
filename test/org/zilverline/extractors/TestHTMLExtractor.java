/*
 * Copyright 2003-2005 Michael Franken, Zilverline.
 *
 * The contents of this file, or the files included with this file, are subject to
 * the current version of ZILVERLINE Collaborative Source License for the
 * Zilverline Search Engine (the "License"); You may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.zilverline.org.
 *
 * See the License for the rights, obligations and
 * limitations governing use of the contents of the file.
 *
 * The Original and Upgraded Code is the Zilverline Search Engine. The developer of
 * the Original and Upgraded Code is Michael Franken. Michael Franken owns the
 * copyrights in the portions it created. All Rights Reserved.
 *
 */

package org.zilverline.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.zilverline.core.ParsedFileInfo;

/**
 * HTNLExtractorTest
 * 
 * @author Michael Franken
 */
public class TestHTMLExtractor extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestHTMLExtractor.class);

    public void testExtractInfo() {
        HTMLExtractor hex = new HTMLExtractor();
        File file = new File("test\\data\\test.html");
        // File file = new File("d:\\books\\problems\\manual_fr.htm2");
        ParsedFileInfo pfi = hex.extractInfo(file);
        assertNotNull(pfi);
        assertEquals("HTML", pfi.getType());
        assertTrue(pfi.getSize() > 0);
        log.debug("Summary: " + pfi.getSummary());
        assertEquals("HTML title", pfi.getTitle());

        char[] text = new char[pfi.getSummary().length()];
        try {
            assertTrue(pfi.getReader().read(text) > 0);
            log.debug("Content with length of summary: " + new String(text));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        assertTrue(pfi.getSummary().length() > 0);
        // assertTrue(new String(text).startsWith(pfi.getSummary()));
        assertTrue(StringUtils.countOccurrencesOf(pfi.getSummary(), "test") > 0);
    }

    public void testGetContentAsFile() {
        HTMLExtractor hex = new HTMLExtractor();
        File file = new File("test\\data\\test.html");
        // File file = new File("d:\\books\\problems\\manual_fr.htm2");
        Reader r = hex.getContent(file);
        assertNotNull(r);

        char[] text = new char[50];
        try {
            assertTrue(r.read(text) > 0);
            log.debug("Content with length of 50: " + new String(text));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        assertTrue(text.length > 0);
    }

    public void testGetContentAsInputStream() {
        try {
            HTMLExtractor tex = new HTMLExtractor();
            InputStream is = new FileInputStream("test\\data\\test.html");
            String txt = tex.getContent(is);
            assertNotNull(txt);
            log.debug(txt.trim());
            assertTrue(txt.length() > 0);
            assertTrue(txt.trim().endsWith("Dit is een test in een HTML document."));
            assertTrue(StringUtils.countOccurrencesOf(txt, "test") > 0);
        }
        catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    public void testGetContentAsString() {
        try {
            HTMLExtractor tex = new HTMLExtractor();
            String html = "<HTML><BODY>here is <B>text</B> to test</BODY></HTML>";
            String txt = tex.getContent(html);
            assertNotNull(txt);
            log.debug(txt);
            assertTrue(txt.length() > 0);
            assertTrue(txt.trim().startsWith("here is text"));
            assertTrue(StringUtils.countOccurrencesOf(txt, "test") > 0);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
