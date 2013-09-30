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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.Result;
import org.zilverline.core.SearchException;
import org.zilverline.core.SearchResult;
import org.zilverline.lucene.BoostFactor;
import org.zilverline.util.Utils;

public class TestSearchService extends AbstractDependencyInjectionSpringContextTests {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestSearchService.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml" };
    }

    public void testDoSearch() {
        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");
        assertTrue("collections must exist", colMan.getCollections().size() > 0);
        SearchServiceImpl service = (SearchServiceImpl) applicationContext.getBean("searchService");
        assertNotNull(service);
        assertNotNull(service.getCollectionManager());
        assertNotNull(service.getFactors());
        BoostFactor bf = new BoostFactor();
        Map factors = new HashMap();
        factors.put("contents", "1");
        factors.put("title", "3.0");
        factors.put("summary", "2");
        factors.put("name", "4");
        bf.setFactors(factors);
        service.setFactors(bf);
        assertNotNull(service.getFactors().getFactors());
        assertEquals("contents boostfactor not 1", (Float) service.getFactors().getFactors().get("contents"), new Float(1.0));
        DocumentCollection thisCollection = (DocumentCollection) colMan.getCollections().toArray()[Utils.pickOne(colMan
            .getCollections().size())];
        assertNotNull(thisCollection);
        log.debug("Searching through " + thisCollection.getName());
        SearchResult result;
        // get one result: the first
        try {
            result = service.doSearch(new String[] { thisCollection.getName() }, "jsp 2.0 taglib", 0, 1);
            int hits = result.getNumberOfHits();
            if (hits > 0) {
                Result[] results = result.getResults();
                assertNotNull(results);
                assertNotNull(results[0]);
                assertTrue(results.length == 1);
                Result res = results[0];
                assertEquals(thisCollection.getName(), res.getCollection());
            }
        }
        catch (SearchException e) {
            fail(e.getMessage());
        }
        // get one result: the second
        try {
            result = service.doSearch(new String[] { thisCollection.getName() }, "jsp 2.0 taglib", 1, 1);
            int hits = result.getNumberOfHits();
            if (hits > 0) {
                Result[] results = result.getResults();
                assertNotNull(results);
                assertNotNull(results[0]);
                assertTrue(results.length == 1);
                Result res = results[0];
                assertEquals(thisCollection.getName(), res.getCollection());
            }
        }
        catch (SearchException e) {
            fail(e.getMessage());
        }
        // get one result: for all collections
        try {
            result = service.doSearch(new String[] { }, "jsp 2.0 taglib", 1, 1);
            int hits = result.getNumberOfHits();
            if (hits > 0) {
                Result[] results = result.getResults();
                assertNotNull(results);
                assertNotNull(results[0]);
                assertTrue(results.length == 1);
                Result res = results[0];
                assertEquals(thisCollection.getName(), res.getCollection());
            }
        }
        catch (SearchException e) {
            fail(e.getMessage());
        }
    }

}
