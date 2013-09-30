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

package org.zilverline.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 * 
 * @author Michael Franken
 */
public class TestFileUtils extends TestCase {
    public TestFileUtils(String arg0) {
        super(arg0);
    }

    public void testRemoveDir() {
        try {
            File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

            // create a directory
            File newDir = new File(tempDirectory, "nklljklfda");

            if (newDir.exists()) {
                assertTrue(FileUtils.removeDir(newDir));
            }
            assertTrue(newDir.mkdirs());
            assertTrue(newDir.isDirectory());

            // create a file in the new Dir
            File newFile = File.createTempFile("tst", ".suf", newDir);

            assertTrue(newFile.isFile());

            // remove all of it
            assertTrue(FileUtils.removeDir(newDir));
            assertFalse("RemoveDir should have removed dir", newDir.exists());
            assertFalse("RemoveDir should have removed file in dir", newFile.exists());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void testGetExtension() {
        assertEquals("Lowercase extension test", "tar", FileUtils.getExtension(new File("test.tar")));
        assertEquals("Uppercase extension test", "TAR", FileUtils.getExtension(new File("test.TAR")));
        assertEquals("No extension test", "", FileUtils.getExtension(new File("test")));
        assertEquals("Null extension test", "", FileUtils.getExtension(null));
    }

    public void testBasename() {
        assertEquals("Lowercase basename test", "test", FileUtils.getBasename(new File("test.tar")));
        assertEquals("Uppercase basename test", "TEST", FileUtils.getBasename(new File("TEST.TAR")));
        assertEquals("No basename test", "", FileUtils.getBasename(new File(".tar")));
        assertEquals("No extension test", "test", FileUtils.getBasename(new File("test")));
        assertEquals("Null basename test", "", FileUtils.getBasename(null));
    }

    public void testGetHash() {
        File contentFile = new File("test\\data\\test.pdf");

        assertTrue(contentFile.isFile());
        assertEquals("MD5 hash test", "0ca6912d6f4331d020ed006939d4b063", FileUtils.getMD5Hash(contentFile));
    }

    public void testisIn() {
        File file = new File("test\\data\\test.pdf");
        File dir = new File("test\\data");

        assertTrue("File is in its parents' directory", FileUtils.isIn(file, dir.getParentFile()));
        assertTrue("File is in its directory", FileUtils.isIn(file, dir));
        assertTrue("File is in its parents' directory", FileUtils.isIn(file, dir.getParentFile()));
        assertFalse("File is not in null directory", FileUtils.isIn(file, null));
        assertFalse("File is not in temp directory", FileUtils.isIn(file, new File(System.getProperty("java.io.tmpdir"))));
        assertFalse("Null File is not in directory", FileUtils.isIn(null, dir));
    }
}
