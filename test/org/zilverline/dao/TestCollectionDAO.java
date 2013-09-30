/*
 *    Copyright (c) 2003 Xebia B.V.,  All rights reserved
 *
 *    This is unpublished proprietary source code of Xebia B.V.
 *    The copyright notice above does not evidence any actual or
 *    intended publication of such source code.
 */

/*
 * Created on 23-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.zilverline.dao;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.service.CollectionManager;

/**
 * DOCUMENT ME!
 * 
 * @author Michael Franken
 */
public class TestCollectionDAO extends AbstractDependencyInjectionSpringContextTests {
    private static Log log = LogFactory.getLog(TestCollectionDAO.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    /**
     * tests retrieval of collection by key.
     */
    public void testSave() {
        CollectionManagerDAO dao = (CollectionManagerDAO) applicationContext.getBean("collectionDao");
        CollectionManager manager = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(dao);
        assertNotNull(manager);
        FileSystemCollection col = new FileSystemCollection();
        col.setName("Test from JUNIT");
        col.setDescription("Description for Collection from JUNIT test");
        col.setContentDir(new File("d:/books/example"));
        col.isKeepCacheWithManagerDefaults();
        assertNull(col.getId());
        manager.addCollection(col);
        assertNotNull(col.getId());
        try {
            dao.store(manager);
        }
        catch (DAOException e) {
            fail(e.getMessage());
        }
        try {
            manager.deleteCollection(col);
            assertNull("collection not deleted", manager.getCollection(col.getId()));
            dao.store(manager);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * tests retrieval of collection by key.
     */
    public void testGetAllCollections() {
        CollectionManagerDAO dao = (CollectionManagerDAO) applicationContext.getBean("collectionDao");
        CollectionManager manager = dao.load();
        List collections = manager.getCollections();
        assertNotNull("collections must exist.", collections);
        assertTrue("collections must exist.", collections.size() > 0);

        DocumentCollection collection = (DocumentCollection) collections.get(0);
        assertNotNull("collection must exist.", collection);
        log.debug(collection.getName());
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertNotNull(colMan);
        colMan.addCollection(collection);
        try {
            collection.init();
        }
        catch (IndexException e) {
            fail(e.getMessage());
        }
        Iterator iter = collections.iterator();
        while (iter.hasNext()) {
            DocumentCollection col = (DocumentCollection) iter.next();
            colMan.addCollection(col);
        }
        try {
            colMan.init();
        }
        catch (IndexException e) {
            fail(e.getMessage());
        }
    }

    public void testDAOException() {
        Exception se = new Exception("test an Exception");
        DAOException de1 = new DAOException("test an DAOExcpetion");
        DAOException de2 = new DAOException("test an DAOExcpetion", se);
        assertNotNull(de1);
        assertNotNull(de2);
        try {
            throw de2;
        }
        catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

}
