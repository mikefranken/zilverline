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

package org.zilverline.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * SysUtilsTest
 * 
 * @author Michael Franken
 */
public class TestSysUtils extends TestCase {

    public void testGetErrorTextById() {
        assertEquals("Access is denied.", SysUtils.getErrorTextById(5));
    }

    public void testExecuteCHM() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "zilverline");
        FileUtils.removeDir(tempDir);
        tempDir.mkdirs();
        assertTrue(SysUtils.execute("hh.exe -decompile .", new File(
            "test/data/Another compiled help file with a longer name than any file.chm"), tempDir));
        assertTrue(new File(tempDir, "calc_list_equiv.htm").exists());
    }

    public void testExecuteRAR() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "zilverline");
        FileUtils.removeDir(tempDir);
        tempDir.mkdirs();
        assertTrue(SysUtils.execute("D:\\WinRAR\\UnRar.exe x -o+ -inul", new File("test/data/test.rar"), tempDir));
        assertTrue(new File(tempDir, "test.txt").exists());
    }

    public void testCanExecute() {
        assertTrue(SysUtils.canExecute("hh.exe -decompile ."));
    }
}
