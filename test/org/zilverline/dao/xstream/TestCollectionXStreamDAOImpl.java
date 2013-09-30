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

package org.zilverline.dao.xstream;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.service.CollectionManager;
import org.zilverline.service.CollectionManagerImpl;

/**
 * @author michael
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TestCollectionXStreamDAOImpl extends AbstractDependencyInjectionSpringContextTests {

    private static Log log = LogFactory.getLog(TestCollectionXStreamDAOImpl.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testSave() {
        CollectionManagerXStreamDAOImpl dao = new CollectionManagerXStreamDAOImpl();
        dao.setBaseDir(new File(System.getProperty("java.io.tmpdir")));
        try {
            CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
            assertNotNull(colMan);
            FileSystemCollection thisCollection = new FileSystemCollection();
            thisCollection.setName("test");
            thisCollection.setContentDir(new File("test\\data"));
            colMan.addCollection(thisCollection);
            dao.store(colMan);
            colMan.deleteCollection(thisCollection);
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

    public void testLoad() {
        CollectionManagerXStreamDAOImpl dao = new CollectionManagerXStreamDAOImpl();
        dao.setBaseDir(new File(System.getProperty("java.io.tmpdir")));
        try {
            CollectionManager colMan = new CollectionManagerImpl();
            assertNotNull(colMan);
            CollectionManager colMan2 = dao.load();
            List collections = colMan2.getCollections();
            assertTrue(collections.size() > 0);
            FileSystemCollection thisCollection = (FileSystemCollection) collections.toArray()[0];
            log.debug(thisCollection);
            colMan.addCollection(thisCollection);
            thisCollection.init();
            log.debug(thisCollection.getName());
            assertTrue("Must delete collection store", new File(dao.getBaseDir(), dao.filename).delete());
        }
        catch (Exception e) {
            fail("Should not happen, Exception: " + e.getMessage());
        }
    }

}
