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

package org.zilverline.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.DocumentCollection;
import org.zilverline.service.CollectionManager;

/**
 * ZilverControllerTest
 * 
 * @author Michael Franken
 */
public class TestZilverController extends AbstractDependencyInjectionSpringContextTests {
    private static Log log = LogFactory.getLog(TestZilverController.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml", "web-servlet.xml" };
    }

    public void testFlushCacheHandler() {
        try {
            ZilverController zilverC = (ZilverController) applicationContext.getBean("zilverController");
            assertNotNull(zilverC);
            CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
            // get testdate collection
            DocumentCollection testdata = colMan.getCollectionByName("testdata");
            assertNotNull(testdata);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("collection", "testdata");
            HttpServletResponse response = new MockHttpServletResponse();
            ModelAndView mv = zilverC.flushCacheHandler(request, response);
            assertNotNull(mv);
        }
        catch (BeansException e) {
            fail(e.getMessage());
        }
        catch (ServletException e) {
            fail(e.getMessage());
        }
    }

    public void testFailFlushCacheHandler() {
        try {
            ZilverController zilverC = (ZilverController) applicationContext.getBean("zilverController");
            assertNotNull(zilverC);
            CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
            // get testdate collection
            DocumentCollection testdata = colMan.getCollectionByName("testdata3");
            assertNull(testdata);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("collection", "testdata3");
            HttpServletResponse response = new MockHttpServletResponse();
            ModelAndView mv = zilverC.flushCacheHandler(request, response);
            assertNotNull(mv);
            fail("Should throw ServletException");
        }
        catch (BeansException e) {
            fail(e.getMessage());
        }
        catch (ServletException e) {
            assertNotNull(e.getMessage());
        }
    }
}
