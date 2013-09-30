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

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.util.FileUtils;

/**
 * @author mfranken
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TestCollectionManagerImpl extends AbstractDependencyInjectionSpringContextTests {

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testAddCollection() {
        CollectionManager colMan = new CollectionManagerImpl();
        FileSystemCollection col1 = new FileSystemCollection();
        // col1.setId(new Long(0));
        col1.setName("collection1");
        FileSystemCollection col2 = new FileSystemCollection();
        // col2.setId(new Long(0));
        col2.setName("collection2");
        assertEquals(0, colMan.getCollections().size());
        colMan.addCollection(col1);
        assertEquals(1, colMan.getCollections().size());
        // two collections with same id: second one replaces first upon addition
        colMan.addCollection(col2);
        assertFalse(col1.getId().equals(col2.getId()));
        assertEquals(2, colMan.getCollections().size());
        colMan.addCollection(col2);
        assertEquals(2, colMan.getCollections().size());
    }

    public void testFileCache2Dir() {
        try {
            FileSystemCollection col = new FileSystemCollection();
            File tempDirectory = new File(System.getProperty("java.io.tmpdir"), "zilverline");

            // create contentDir
            File contentDirectory = new File(tempDirectory, "content");
            if (!contentDirectory.exists()) {
                assertTrue("must be able to create content at: " + contentDirectory, contentDirectory.mkdirs());
            }
            col.setContentDir(contentDirectory);
            // create cacheDir
            File cacheDirectory = new File(tempDirectory, "cache");
            col.setCacheDir(cacheDirectory);
            if (!cacheDirectory.exists()) {
                assertTrue("must be able to create cache at: " + cacheDirectory, cacheDirectory.mkdirs());
            }

            // create a file in contentDir
            File file = File.createTempFile("test", ".zip", contentDirectory);
            assertTrue(file.isFile());
            // test file2Dir since the file is within contentDir, it should be mapped to the cache
            File dir = CollectionManagerImpl.file2CacheDir(file, col);
            assertTrue(dir.isDirectory());
            // String resultFile = file.getName().replace('.', '_');
            // assertEquals(dir.getCanonicalPath(), new File(cacheDirectory, resultFile).getCanonicalPath());
            // clean up
            assertTrue("must be able to remove dir from: " + dir, FileUtils.removeDir(dir));

            // test file2Dir since the file is within cacheDir, it should stay in the cache
            file = File.createTempFile("test2", ".zip", cacheDirectory);
            assertTrue(file.isFile());
            dir = CollectionManagerImpl.file2CacheDir(file, col);
            assertTrue(dir.isDirectory());
            // resultFile = file.getName().replace('.', '_');
            // assertEquals(dir.getCanonicalPath(), new File(cacheDirectory, resultFile).getCanonicalPath());

            // clean up
            assertTrue("must be able to remove dir from: " + dir, FileUtils.removeDir(dir));
            assertTrue("must be able to remove cache from: " + cacheDirectory, FileUtils.removeDir(cacheDirectory));
            assertTrue("must be able to remove content from: " + contentDirectory, FileUtils.removeDir(contentDirectory));
            assertTrue("must be able to remove temp from: " + tempDirectory, FileUtils.removeDir(tempDirectory));
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

    public void testFile2CacheDirFromDrive() {
        try {
            FileSystemCollection col = new FileSystemCollection();
            File tempDirectory = new File(System.getProperty("java.io.tmpdir"), "zilverline");
            File contentDirectory = null;

            // create contentDir
            File[] drives = File.listRoots();

            for (int i = 0; i < drives.length; i++) {
                if (drives[i].getAbsolutePath().equalsIgnoreCase("A:\\")) {
                    continue;
                }

                if (drives[i].getAbsolutePath().equalsIgnoreCase("B:\\")) {
                    continue;
                }

                if (drives[i].canWrite()) {
                    contentDirectory = drives[i];
                    break;
                }
            }
            col.setContentDir(contentDirectory);

            if (!contentDirectory.exists()) {
                assertTrue("must be able to create content at: " + contentDirectory, contentDirectory.mkdirs());
            }
            col.setContentDir(contentDirectory);
            // create cacheDir
            File cacheDirectory = new File(tempDirectory, "cache");
            col.setCacheDir(cacheDirectory);
            if (!cacheDirectory.exists()) {
                assertTrue("must be able to create cache at: " + cacheDirectory, cacheDirectory.mkdirs());
            }

            // create a file in contentDir
            File tempFile = File.createTempFile("test", ".zip", contentDirectory);

            assertTrue(tempFile.isFile());
            assertTrue(tempFile.exists());

            // test file2Dir since the file is within contentDir, it should be
            // mapped to the cache
            File dir = CollectionManagerImpl.file2CacheDir(tempFile, col);

            assertTrue(dir.isDirectory());

            // clean up
            assertTrue("must be able to remove dir from: " + dir, FileUtils.removeDir(dir));
            assertTrue("must be able to remove cache from: " + cacheDirectory, FileUtils.removeDir(cacheDirectory));
            assertTrue("must be able to remove temp from: " + tempDirectory, FileUtils.removeDir(tempDirectory));
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.toString());
        }
    }

    public void testExpandArchive() {
        CollectionManager manager = (CollectionManagerImpl) applicationContext.getBean("collectionMan");
        assertNotNull(manager);
        assertTrue("collections must exist", manager.getCollections().size() > 0);
        FileSystemCollection col = (FileSystemCollection) manager.getCollections().get(0);
        try {
            assertTrue(manager.expandArchive(col, new File("test/data/test.zip")));
        }
        catch (IndexException e) {
            fail(e.getMessage());
        }
    }
}
