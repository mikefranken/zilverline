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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.util.StringUtils;

import org.zilverline.service.CollectionManager;

/**
 * Test class for Collection, self documenting.
 * 
 * @author Michael Franken
 * 
 * @see org.zilverline.core.FileSystemCollection
 */
public class TestFileSystemCollection extends AbstractDependencyInjectionSpringContextTests {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestFileSystemCollection.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testName() {
        FileSystemCollection col = new FileSystemCollection();
        assertEquals("", col.getName());
        col.setName("test");
        assertEquals("test", col.getName());
    }

    public void testNoURL() {
        FileSystemCollection col = new FileSystemCollection();
        assertNull(col.getUrlDefault());
        col.setContentDir(new File(System.getProperty("java.io.tmpdir")));
        col.setUrl(null);
        assertTrue(new File(System.getProperty("java.io.tmpdir")).exists());
        assertTrue("need a URL", StringUtils.hasLength(col.getUrlDefault()));
        assertTrue(col.getUrlDefault().endsWith("/"));
    }

    public void testURL() {
        FileSystemCollection col = new FileSystemCollection();
        assertNull(col.getUrlDefault());
        String testString = "http://localhost/books";
        col.setUrl(testString);
        assertEquals("http://localhost/books/", col.getUrlDefault());
        assertTrue(col.getUrlDefault().endsWith("/"));
    }

    public void testNoCacheURL() {
        FileSystemCollection col = new FileSystemCollection();
        assertNotNull(col.getCacheDirWithManagerDefaults());
        assertNotNull(col.getCacheUrlWithManagerDefaults());
        col.setCacheDir(new File(System.getProperty("java.io.tmpdir")));
        col.setCacheUrl(null);
        assertTrue(new File(System.getProperty("java.io.tmpdir")).exists());
        assertTrue("need a URL", StringUtils.hasLength(col.getCacheUrlWithManagerDefaults()));
        assertTrue(col.getCacheUrlWithManagerDefaults().endsWith("/"));
    }

    public void testCacheURL() {
        FileSystemCollection col = new FileSystemCollection();
        assertNull(col.getUrlDefault());
        String testString = "http://localhost/cachedBooks";
        col.setCacheUrl(testString);
        assertEquals("http://localhost/cachedBooks/", col.getCacheUrlWithManagerDefaults());
        assertTrue(col.getCacheUrlWithManagerDefaults().endsWith("/"));
    }

    public final void testContentDir() {
        try {
            CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
            List collections = colMan.getCollections();

            for (Iterator iter = collections.iterator(); iter.hasNext();) {
                DocumentCollection col = (DocumentCollection) iter.next();

                assertTrue(col.getName() + " does not exist", col.isIndexValid());
                assertTrue(col.getName() + " with url: '" + col.getUrlDefault() + "' does not end with /", col.getUrlDefault()
                    .endsWith("/"));
            }
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

    public final void testCollectionAddToManager() {
        try {
            CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
            int size = colMan.getCollections().size();

            FileSystemCollection col1 = new FileSystemCollection();
            col1.setName("collection1");
            colMan.addCollection(col1);
            log.debug("New collection has got Id: " + col1.getId());
            assertEquals(size + 1, colMan.getCollections().size());

            FileSystemCollection col2 = new FileSystemCollection();
            col2.setName("collection2");
            // give the new Collection an existing ID, then add it, it should replace instead of being added
            col2.setId(col1.getId());
            // test overridden equals method
            assertTrue(col1.equals(col2));
            colMan.addCollection(col2);
            assertEquals(size + 1, colMan.getCollections().size());
            colMan.deleteCollection(col1);
            colMan.deleteCollection(col2);
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

    public final void testCacheDir() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        List collections = colMan.getCollections();

        for (Iterator iter = collections.iterator(); iter.hasNext();) {
            DocumentCollection col = (DocumentCollection) iter.next();
            // the following assertion should hold if the cacheDir exists, and is a Directory
            if (col.getCacheDirWithManagerDefaults().exists()) {
                assertTrue(col.getCacheUrlWithManagerDefaults(), col.getCacheUrlWithManagerDefaults().endsWith("/"));
            }
        }
    }

    public void testAllCollections() throws Exception {
        CollectionManager c = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(c);

        ListIterator li = c.getCollections().listIterator();
        while (li.hasNext()) {
            DocumentCollection col = (DocumentCollection) li.next();
            log.debug("Collection " + col.getName());
            if (col instanceof FileSystemCollection) {
                assertTrue(col.getName(), ((FileSystemCollection) col).getContentDir().isDirectory());
                log.debug(((FileSystemCollection) col).getContentDir() + ", " + col.getIndexDirWithManagerDefaults() + ", "
                    + col.getCacheDirWithManagerDefaults());
                log.debug(((FileSystemCollection) col).getContentDir().getAbsoluteFile());
            }
            log.debug(col.getName() + " has %%% " + col.getNumberOfDocs() + " documents.");
        }
    }

}
