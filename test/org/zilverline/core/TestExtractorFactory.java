/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
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

package org.zilverline.core;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test class for Collection, self documenting.
 * 
 * @author Michael Franken
 * 
 * @see org.zilverline.core.FileSystemCollection
 */
public class TestExtractorFactory extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestExtractorFactory.class);

    public void testMappings() {
        Properties props = new Properties();

        ExtractorFactory ef = new ExtractorFactory();

        // add handler for zip
        props.put("PDF", "org.zilverline.extractors.PDFExtractor");
        props.put("doc", "org.zilverline.extractors.WordExtractor");
        props.put("html", "org.zilverline.extractors.HTMLExtractor");
        props.put("htm", "org.zilverline.extractors.HTMLExtractor");
        props.put("txt", "org.zilverline.extractors.TextExtractor");

        ef.setCaseSensitive(false);
        ef.setMappings(props);

        Map mappings = ef.getMappings();

        assertTrue("Testing whether keys are stored in lowercase", mappings.containsKey("pdf"));
        assertEquals((String) mappings.get("pdf"), "org.zilverline.extractors.PDFExtractor");

        ef.setCaseSensitive(true);
        ef.setMappings(props);
        mappings = ef.getMappings();
        assertFalse("Testing whether keys are not stored in lowercase", mappings.containsKey("pdf"));
        assertTrue("Testing whether keys are stored in  original case", mappings.containsKey("PDF"));
        assertEquals((String) mappings.get("PDF"), "org.zilverline.extractors.PDFExtractor");
    }

    public void testFactory() {
        Properties props = new Properties();

        ExtractorFactory ef = new ExtractorFactory();

        ef.setCaseSensitive(false);

        // add handler for zip
        props.put("pdf", "org.zilverline.extractors.PDFExtractor");
        props.put("doc", "org.zilverline.extractors.WordExtractor");
        props.put("rtf", "org.zilverline.extractors.RTFExtractor");
        props.put("html", "org.zilverline.extractors.HTMLExtractor");
        props.put("htm", "org.zilverline.extractors.HTMLExtractor");
        props.put("txt", "org.zilverline.extractors.TextExtractor");
        props.put("", "org.zilverline.extractors.TextExtractor");
        ef.setMappings(props);

        File pdf = new File("test\\data\\test.pdf");

        assertTrue(pdf.isFile());

        Extractor ex = ef.createExtractor(pdf);

        log.debug(ex.getClass().getName());

        ParsedFileInfo file = ex.extractInfo(pdf);

        log.debug("###########################################" + file.getType());

        File txt = new File("test\\data\\some text file with a very nasty long filename.txt");

        assertTrue(txt.isFile());
        ex = ef.createExtractor(txt);
        log.debug(ex.getClass().getName());
        file = ex.extractInfo(txt);
        log.debug("###########################################" + file.getType());

        txt = new File("test\\data\\readme");
        assertTrue(txt.isFile());
        ex = ef.createExtractor(txt);
        log.debug(ex.getClass().getName());
        file = ex.extractInfo(txt);
        log.debug("###########################################" + file.getType());

        File word = new File("test\\data\\test.doc");

        assertTrue(word.isFile());
        ex = ef.createExtractor(word);
        log.debug(ex.getClass().getName());
        file = ex.extractInfo(word);
        log.debug("###########################################" + file.getType());

        File rtf = new File("test\\data\\test.rtf");

        assertTrue(rtf.isFile());
        ex = ef.createExtractor(rtf);
        assertEquals("org.zilverline.extractors.RTFExtractor", ex.getClass().getName());
        file = ex.extractInfo(rtf);
        log.debug("###########################################" + file.getType());

        File html = new File("test\\data\\test.html");

        assertTrue(html.isFile());
        ex = ef.createExtractor(html);
        assertEquals("org.zilverline.extractors.HTMLExtractor", ex.getClass().getName());
        file = ex.extractInfo(html);
        log.debug("###########################################" + file.getType());
    }

    public void testGetMimeType() {
        File dir = new File("test\\data");
        File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (int i = 0; i < allFiles.length; i++) {
                File file = allFiles[i];
                if (file.isFile()) {
                    log.debug("File type of " + file.getName() + " is: " + ExtractorFactory.getMimeType(file));
                }
            }
        }
    }

}
