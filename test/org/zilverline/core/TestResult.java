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

import java.util.Date;

import junit.framework.TestCase;

/**
 * Test class for Result, self documenting.
 * 
 * @author Michael Franken
 * 
 * @see org.zilverline.core.Result
 */
public class TestResult extends TestCase {
    /**
     * Creates a new TestResult object.
     * 
     * @param arg0 DOCUMENT ME!
     */
    public TestResult(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testTitle() {
        Result res = new Result();

        assertEquals("", res.getTitle());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testName() {
        Result res = new Result();

        assertEquals("", res.getName());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testPath() {
        Result res = new Result();

        assertEquals("", res.getPath());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testSummary() {
        Result res = new Result();

        assertEquals("", res.getSummary());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testScoreString() {
        Result res = new Result();

        assertEquals("", res.getScoreString());
        assertEquals(0f, res.getScore(), 0f);
        res.setScore(0.13f);
        assertEquals(0.13f, res.getScore(), 0f);
        assertEquals("13%", res.getScoreString());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testModificationDate() {
        Result res = new Result();

        assertEquals(null, res.getModificationDate());

        Date d = new Date();
        String testString = d.toString();

        res.setModificationDate(d);
        assertEquals(d, res.getModificationDate());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testType() {
        Result res = new Result();

        assertEquals("", res.getType());

        String testString = "PDF";

        res.setType(testString);
        assertEquals(testString, res.getType());
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testSizeAsString() {
        Result res = new Result();

        assertEquals("", res.getSizeAsString());
        assertEquals("", res.getSize());
        res.setSize("123");
        assertEquals("123", res.getSize());
        assertEquals("123B", res.getSizeAsString());
        res.setSize("12345");
        assertEquals("12KB", res.getSizeAsString());
        res.setSize("1234567");
        assertEquals("1MB", res.getSizeAsString());
        res.setSize("1234567890");
        assertEquals("1GB", res.getSizeAsString());
        res.setSize("");
        assertEquals("", res.getSizeAsString());
    }
}
