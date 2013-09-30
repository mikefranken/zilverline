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

package org.zilverline.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.service.CollectionManager;

/**
 * @author Michael Franken
 * @since 31-3-2005
 */
public class TestCustomCollectionEditor extends AbstractDependencyInjectionSpringContextTests {
    private static Log log = LogFactory.getLog(TestCustomCollectionEditor.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testCustomCollectionEditorWithString() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(colMan);
        assertTrue("collections must exist", colMan.getCollections().size() > 0);

        SearchForm sf = new SearchForm();
        BeanWrapper bw = new BeanWrapperImpl(sf);
        bw.registerCustomEditor(CollectionTriple[].class, "collections", new CustomCollectionEditor(colMan));
        try {
            bw.setPropertyValue("collections", "problems");
        }
        catch (BeansException ex) {
            fail("Should not throw BeansException: " + ex.getMessage());
        }
        CollectionTriple[] col3s = (CollectionTriple[]) bw.getPropertyValue("collections");
        assertEquals(colMan.getCollections().size(), col3s.length);
        assertEquals("testdata", col3s[0].getName());
    }

    public void testCustomCollectionEditorWithStrings() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(colMan);
        assertTrue("collections must exist", colMan.getCollections().size() > 0);
        log.debug("test");

        SearchForm sf = new SearchForm();
        BeanWrapper bw = new BeanWrapperImpl(sf);
        bw.registerCustomEditor(CollectionTriple[].class, "collections", new CustomCollectionEditor(colMan));
        try {
            bw.setPropertyValue("collections", "problems, testdata");
        }
        catch (BeansException ex) {
            fail("Should not throw BeansException: " + ex.getMessage());
        }
        CollectionTriple[] col3s = (CollectionTriple[]) bw.getPropertyValue("collections");
        log.debug(col3s);
        assertEquals(colMan.getCollections().size(), col3s.length);
        for (int i = 0; i < colMan.getCollections().size(); i++) {
            assertTrue(col3s[0].isSelected());
        }
    }

    public void testCustomCollectionEditorWithStringArrayOfOne() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(colMan);
        assertTrue("collections must exist", colMan.getCollections().size() > 0);

        SearchForm sf = new SearchForm();
        BeanWrapper bw = new BeanWrapperImpl(sf);
        bw.registerCustomEditor(CollectionTriple[].class, "collections", new CustomCollectionEditor(colMan));
        try {
            bw.setPropertyValue("collections", new String[] { "problems" });
        }
        catch (BeansException ex) {
            fail("Should not throw BeansException: " + ex.getMessage());
        }
        CollectionTriple[] col3s = (CollectionTriple[]) bw.getPropertyValue("collections");
        assertEquals(colMan.getCollections().size(), col3s.length);
        assertEquals("testdata", col3s[0].getName());
    }

    public void testCustomCollectionEditorWithStringArrayOfTwo() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(colMan);
        assertTrue("collections must exist", colMan.getCollections().size() > 0);

        SearchForm sf = new SearchForm();
        BeanWrapper bw = new BeanWrapperImpl(sf);
        bw.registerCustomEditor(CollectionTriple[].class, "collections", new CustomCollectionEditor(colMan));
        try {
            bw.setPropertyValue("collections", new String[] { "problems", "testdata" });
        }
        catch (BeansException ex) {
            fail("Should not throw BeansException: " + ex.getMessage());
        }
        CollectionTriple[] col3s = (CollectionTriple[]) bw.getPropertyValue("collections");
        assertEquals(colMan.getCollections().size(), col3s.length);
        assertEquals("testdata", col3s[0].getName());
    }

}
