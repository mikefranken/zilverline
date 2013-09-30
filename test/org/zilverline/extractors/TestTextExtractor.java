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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.zilverline.core.ParsedFileInfo;

/**
 * TestTextExtractor
 * 
 * @author Michael Franken
 */
public class TestTextExtractor extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestTextExtractor.class);

    public void testGetContent() {
        TextExtractor tex = new TextExtractor();
        File file = new File("test\\data\\readme");
        ParsedFileInfo pfi = tex.extractInfo(file);
        assertNotNull(pfi);
        assertEquals("TEXT", pfi.getType());
        assertTrue(pfi.getSize() > 0);
        log.debug("Summary: " + pfi.getSummary());

        char[] text = new char[pfi.getSummary().length()];
        try {
            assertTrue(pfi.getReader().read(text) > 0);
            log.debug("Content with length of summary: " + new String(text));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        assertTrue(new String(text).startsWith(pfi.getSummary()));
        assertTrue(StringUtils.countOccurrencesOf(pfi.getSummary(), "test") > 0);
    }

    public void testGetContentAsInputStream() {
        try {
            TextExtractor tex = new TextExtractor();
            InputStream is = new FileInputStream("test\\data\\readme");
            String txt = tex.getContent(is);
            assertNotNull(txt);
            log.debug(txt);
            assertTrue(txt.length() > 0);
            assertTrue(txt.startsWith("Dit is een test in een TEXT document."));
            assertTrue(StringUtils.countOccurrencesOf(txt, "test") > 0);
        }
        catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }
}
