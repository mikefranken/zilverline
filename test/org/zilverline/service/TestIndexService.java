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

package org.zilverline.service;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.core.ExtractorFactory;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.Handler;
import org.zilverline.core.IndexException;
import org.zilverline.util.FileUtils;

/**
 * IndexServiceTest
 * 
 * @author Michael Franken
 */
public class TestIndexService extends AbstractDependencyInjectionSpringContextTests {

    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestSearchService.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public final void testDoIndex() {
        IndexServiceImpl service = (IndexServiceImpl) applicationContext.getBean("indexService");
        assertNotNull(service);
        try {
            File contentDirectory = new File("test\\data\\");

            assertTrue(contentDirectory.exists());
            assertTrue(contentDirectory.isDirectory());

            CollectionManager manager = new CollectionManagerImpl();
            File tempDirectory = new File(System.getProperty("java.io.tmpdir"), "zilverline");
            File cacheBaseDir = new File(tempDirectory, "cache");
            File indexBaseDir = new File(tempDirectory, "index");

            manager.setCacheBaseDir(cacheBaseDir);
            manager.setIndexBaseDir(indexBaseDir);

            FileSystemCollection col1 = new FileSystemCollection();

            col1.setName("test");
            assertEquals("test", col1.getName());
            col1.setContentDir(contentDirectory);
            col1.setKeepCache(true);
            col1.setDescription("A number of different file formats for testing purposes");
            manager.addCollection(col1);
            log.debug(col1.getName() + " has ^^^^^^^^^^ " + col1.getNumberOfDocs() + " documents.");
            col1.init();
            log.debug(col1.getName() + " has ^^^^^^^^^^ " + col1.getNumberOfDocs() + " documents.");

            assertEquals(col1.getContentDir().getAbsolutePath(), contentDirectory.getAbsolutePath());
            assertEquals(col1.getCacheDirWithManagerDefaults().getAbsolutePath(), new File(cacheBaseDir, "test").getAbsolutePath());
            assertEquals(col1.getIndexDirWithManagerDefaults().getAbsolutePath(), new File(indexBaseDir, "test").getAbsolutePath());

            assertTrue(col1.existsOnDisk());

            // remove the index and cache
            File indexDir = col1.getIndexDirWithManagerDefaults();
            File cacheDir = col1.getCacheDirWithManagerDefaults();

            if (indexDir.exists()) {
                assertTrue(FileUtils.removeDir(indexDir));
            }

            if (cacheDir.exists()) {
                assertTrue(FileUtils.removeDir(cacheDir));
            }

            Properties extprops = new Properties();

            ExtractorFactory ef = new ExtractorFactory();

            ef.setCaseSensitive(false);

            // add handler extractors
            extprops.put("pdf", "org.zilverline.extractors.PDFExtractor");
            extprops.put("doc", "org.zilverline.extractors.WordExtractor");
            extprops.put("rtf", "org.zilverline.extractors.RTFExtractor");
            extprops.put("html", "org.zilverline.extractors.HTMLExtractor");
            extprops.put("htm", "org.zilverline.extractors.HTMLExtractor");
            extprops.put("txt", "org.zilverline.extractors.TextExtractor");
            extprops.put("xls", "org.zilverline.extractors.ExcelExtractor");
            extprops.put("ppt", "org.zilverline.extractors.PowerPointExtractor");
            ef.setMappings(extprops);

            col1.init();
            assertEquals(0, col1.getNumberOfDocs());
            assertFalse(col1.isIndexValid());

            // define archive handler, no handlers yet
            Handler handler = new Handler();
            Map props = new Properties();
            handler.setCaseSensitive(false);
            handler.setMappings(props);
            manager.setArchiveHandler(handler);

            manager.setFactory(ef);
            service.setCollectionManager(manager);
            service.doIndex(new String[] { col1.getName() }, true);
            while (col1.isIndexingInProgress()) {
                Thread.sleep(100);
                log.debug("zzz");
            }
            assertTrue(col1.isIndexValid());

            // no archive handler, so no unpacking of archives
            // 8 docs: a doc, a rtf, a pdf, a html, a txt, a xls, a ppt and a txt in a dir
            int num = 8;

            assertEquals(num, col1.getNumberOfDocs());
            assertFalse(cacheDir.exists());

            // add handler for zip
            props.put("zip", "");
            handler.setCaseSensitive(false);
            handler.setMappings(props);
            service.doIndex(new String[] { col1.getName() }, true);
            while (col1.isIndexingInProgress()) {
                Thread.sleep(100);
                log.debug("zzz");
            }

            // 4 files in: a zip, a zip in zip, a zip in a dir, and a zip with a dir.
            // Two of them are identical, so 1 gets skipped. Zips themselves are added as well
            num += 6;
            assertEquals("Testing zip support: ", num, col1.getNumberOfDocs());

            // add handler for rar
            props.put("RAR", "UnRar.exe x -o+ -inul");
            handler.setMappings(props);
            assertTrue("Testing casesensitivity of mappings", handler.getMappings().containsKey("rar"));
            service.doIndex(new String[] { col1.getName() }, true);
            while (col1.isIndexingInProgress()) {
                Thread.sleep(100);
                log.debug("zzz");
            }

            // 2 more file in rar: 1 duplicate
            num += 2;

            // Using funny name: "some RAR file with a very nasty long filename.rar" accepted
            assertEquals("Testing rar support: ", num, col1.getNumberOfDocs());
            assertTrue(cacheDir.exists());

            // add handler for chm
            props.put("CHM", "hh.exe -decompile .");
            handler.setCaseSensitive(true);
            handler.setMappings(props);
            assertFalse("Testing casesensitivity of mappings", handler.getMappings().containsKey("chm"));

            //props.put("chm", "hh.exe -decompile .");
            handler.setCaseSensitive(false);
            handler.setMappings(props);
            assertTrue("Testing casesensitivity of mappings", handler.getMappings().containsKey("chm"));
            service.doIndex(new String[] { col1.getName() }, true);
            while (col1.isIndexingInProgress()) {
                Thread.sleep(100);
                log.debug("zzz");
            }

            // 13 individual files in "Another compiled help file with a longer
            // name than any file.chm"
            num += 14;
            assertEquals("Testing chm support: ", num, col1.getNumberOfDocs());

            // add handler for rar
            props.put("hlp", "d:\\helpdeco\\helpdeco.exe /r /y /n");
            handler.setMappings(props);
            assertTrue("Testing casesensitivity of mappings", handler.canUnPack("HLP"));
            service.doIndex(new String[] { col1.getName() }, true);
            while (col1.isIndexingInProgress()) {
                Thread.sleep(100);
                log.debug("zzz");
            }

            // 1 more file EXNED31.HLP: archive and extracted RTF
            num += 2;

            assertEquals("Testing hlp support: ", num, col1.getNumberOfDocs());
            assertTrue(cacheDir.exists());
            
            assertTrue("Should delete tempDirectory: " + tempDirectory, FileUtils.removeDir(tempDirectory));
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

    public void testIndex() {
        IndexServiceImpl service = (IndexServiceImpl) applicationContext.getBean("indexService");
        assertNotNull(service);
        try {
            service.reIndex();
            service.index();
            service.store();
        }
        catch (IndexException e) {
            fail(e.getMessage());
        }
    }

//    public final void testPerformanceDoIndex() {
//        try {
//            File contentDirectory = new File("d:/spring-framework-1.2.7/docs");
//
//            CollectionManager manager = new CollectionManagerImpl();
//            File tempDirectory = new File(System.getProperty("java.io.tmpdir"), "zilverline");
//            File cacheBaseDir = new File(tempDirectory, "cache");
//            File indexBaseDir = new File(tempDirectory, "index");
//
//            manager.setCacheBaseDir(cacheBaseDir);
//            manager.setIndexBaseDir(indexBaseDir);
//
//            FileSystemCollection col1 = new FileSystemCollection();
//
//            col1.setName("test");
//            assertEquals("test", col1.getName());
//            col1.setContentDir(contentDirectory);
//            col1.setDescription("A number of HTML for performance testing purposes");
//            manager.addCollection(col1);
//            assertTrue(col1.existsOnDisk());
//
//            // remove the index and cache
//            File indexDir = col1.getIndexDirWithManagerDefaults();
//            File cacheDir = col1.getCacheDirWithManagerDefaults();
//
//            if (indexDir.exists()) {
//                assertTrue(FileUtils.removeDir(indexDir));
//            }
//
//            if (cacheDir.exists()) {
//                assertTrue(FileUtils.removeDir(cacheDir));
//            }
//
//            Properties extprops = new Properties();
//
//            ExtractorFactory ef = new ExtractorFactory();
//
//            ef.setCaseSensitive(false);
//
//            // add handler extractors
//            String exStr = "org.zilverline.extractors.HTMLExtractor"; // 1:05
//            //String exStr = "org.zilverline.extractors.HTMLLuceneExtractor"; // 1:19
//            //String exStr = "org.zilverline.extractors.HTMLJTidyExtractor"; // 2:16
//            extprops.put("html", exStr);
//            extprops.put("htm", exStr);
//            ef.setMappings(extprops);
//
//            col1.init();
//            assertEquals(0, col1.getNumberOfDocs());
//            assertFalse(col1.isIndexValid());
//
//            // define archive handler, no handlers yet
//            Handler handler = new Handler();
//            Map props = new Properties();
//            handler.setCaseSensitive(false);
//            handler.setMappings(props);
//            manager.setArchiveHandler(handler);
//
//            manager.setFactory(ef);
//            col1.index(true);
//            assertTrue(col1.isIndexValid());
//
//            assertEquals("Testing chm support: ", 2930, col1.getNumberOfDocs());
//            assertTrue("Should delete tempDirectory: " + tempDirectory, FileUtils.removeDir(tempDirectory));
//        }
//        catch (Exception e) {
//            fail("Should not happen, Exception: " + e.getMessage());
//        }
//    }

}
