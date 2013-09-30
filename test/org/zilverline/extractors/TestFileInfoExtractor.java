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
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zilverline.core.ParsedFileInfo;

/**
 * TestFileInfoExtractor.
 * 
 * @author Michael Franken
 */
public class TestFileInfoExtractor extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestFileInfoExtractor.class);

    public void testGetContent() {
        FileInfoExtractor tex = new FileInfoExtractor();
        File file = new File("test\\data\\test.xls");
        ParsedFileInfo pfi = tex.extractInfo(file);
        assertNotNull(pfi);
        assertEquals("FILE", pfi.getType());
        assertEquals("test", pfi.getTitle());
        assertNotNull(pfi.getFile());
        assertTrue(pfi.getSize() > 0);
        assertTrue("".equals(pfi.getSummary()));
        assertTrue(pfi.getReader() == null);
    }

    public void testGetContentAsInputStream() {
        try {
            FileInfoExtractor tex = new FileInfoExtractor();
            InputStream is = new FileInputStream("test\\data\\test.xls");
            String txt = tex.getContent(is);
            assertTrue("".equals(txt));
        }
        catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }
}
