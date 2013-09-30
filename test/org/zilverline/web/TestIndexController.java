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

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.service.CollectionManager;

public class TestIndexController extends AbstractDependencyInjectionSpringContextTests {
    private static Log log = LogFactory.getLog(TestIndexController.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml", "web-servlet.xml" };
    }

    public void testIndexController() throws Exception {
        assertTrue(applicationContext.containsBean("indexController"));
        assertTrue(applicationContext.containsBean("collectionMan"));

        IndexController ixC = (IndexController) applicationContext.getBean("indexController");
        CollectionManager colM = (CollectionManager) applicationContext.getBean("collectionMan");

        assertEquals(ixC.getCollectionManager(), colM);
        log.debug(ixC.getApplicationContext().getBeanDefinitionNames());

        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");

        log.debug("Index is at: " + colMan.getIndexBaseDir());
        // get the testdata collection
        FileSystemCollection col = (FileSystemCollection) colMan.getCollectionByName("testdata");

        assertNotNull(col);

        CollectionForm cf = new CollectionForm();

        cf.setNames(new String[] { col.getName() });

        // full index
        cf.setFullIndex(true);

        ModelAndView mv = ixC.onSubmit(cf);
        assertTrue(col.isIndexValid());
        while (col.isIndexingInProgress()) {
            Thread.sleep(100);
            // col.stopRequest();
            log.debug("zzz");
        }

        assertEquals("14 docs", 14, col.getNumberOfDocs());
        assertEquals("returned correct view name", mv.getViewName(), "collections");
        assertTrue(mv.getModel().containsKey("collections"));
    }
}
