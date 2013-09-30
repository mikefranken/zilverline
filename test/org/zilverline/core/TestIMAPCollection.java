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

package org.zilverline.core;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.service.CollectionManager;

/**
 * IMAPCollectionTest
 * 
 * @author Michael Franken
 */
public class TestIMAPCollection extends AbstractDependencyInjectionSpringContextTests {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestIMAPCollection.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testConnect() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        IMAPCollection col = new IMAPCollection();
        col.setName("michael@franken.ws");
        try {
            colMan.addCollection(col);
            col.setAnalyzer("org.apache.lucene.analysis.standard.StandardAnalyzer");
            col.setIndexDir(new File("d:/temp/zilverline/mail"));
            col.setHost("xxxxx");
            col.setUser("xxx");
            col.setPassword("xxx");
            col.setFolder("test");
            col.init();
            colMan.deleteCollection(col);
        }
        catch (IndexException e) {
            fail("Can not initialize IMAP collection: " + col.getName() + ", " + e.getMessage());
        }

        try {
            col.index(true);
            assertTrue(col.isIndexValid());
            int numberOfMails = col.getNumberOfDocs();
            col.index(false);
            assertTrue(col.isIndexValid());
            assertEquals(numberOfMails, col.getNumberOfDocs());
        }
        catch (IndexException e) {
            fail("Can not index IMAP collection: " + col.getName() + ", " + e.getMessage());
        }
    }

}
