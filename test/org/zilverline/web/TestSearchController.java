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

/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * Licensed under the Zilverline Collabarative License, Version 1.3.1z (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.zilverline.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zilverline.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.core.Result;
import org.zilverline.service.CollectionManager;

/**
 * DOCUMENT ME!
 * 
 * @author michael To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TestSearchController extends AbstractDependencyInjectionSpringContextTests {
    private static Log log = LogFactory.getLog(TestSearchController.class);

    protected String[] getConfigLocations() {
        return new String[] { "applicationContext.xml", "web-servlet.xml" };
    }

    /*
     * Test for ModelAndView onSubmit(HttpServletRequest, HttpServletResponse, Object, BindException)
     */
    public void testOnSubmit() throws ServletException {
        SearchController searchC = (SearchController) applicationContext.getBean("searchController");

        assertNotNull(searchC);

        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");

        // get testdate collection
        DocumentCollection testdata = colMan.getCollectionByName("testdata");

        assertNotNull(testdata);
        int numberOfDocs = 14;
        try {
            if (!testdata.isIndexValid() || testdata.getNumberOfDocs() != numberOfDocs) {
                testdata.index(true);
            }
            assertTrue(testdata.isIndexValid());
            assertEquals(numberOfDocs, testdata.getNumberOfDocs());
        }
        catch (IndexException e) {
            fail(e.getMessage());
        }

        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        SearchForm sf = new SearchForm();

        sf.setCollections(new CollectionTriple[] { new CollectionTriple(testdata, true) });
        sf.setStartAt(-1);
        sf.setQuery("test");

        ModelAndView mv = searchC.onSubmit(request, response, sf, new BindException(sf, "test"));

        assertEquals("returned correct view name", mv.getViewName(), "search");
        assertTrue(mv.getModel().containsKey("results"));

        Result[] results = (Result[]) mv.getModel().get("results");

        assertNotNull(results);
        assertEquals("11 results", 11, results.length);

        if (results.length > 0) {
            Result firstResult = results[0];

            assertEquals(testdata.getName(), firstResult.getCollection());
        }
    }

    /*
     * Test for ModelAndView onSubmit(HttpServletRequest, HttpServletResponse, Object, BindException)
     */
    public void testEncodingOnSubmit() throws ServletException {
        AbstractZilverController searchC = (AbstractZilverController) applicationContext.getBean("searchController");

        assertNotNull(searchC);

        CollectionManager colMan = (CollectionManager) applicationContext.getBean("collectionMan");

        // get collection with chinese docs
        FileSystemCollection col = (FileSystemCollection) colMan.getCollectionByName("chinese docs");
        // assertNotNull("Collection " + col.getName() + " exists", col);
        //
        // MockHttpServletRequest request = new MockHttpServletRequest("GET",
        // "/foobar.html");
        // MockHttpServletResponse response = new MockHttpServletResponse();
        // request.setCharacterEncoding("UTF-8");
        // response.setHeader("Content-Type", "text/html; charset=UTF-8");
        //
        // SearchForm sf = new SearchForm();
        // request.addParameter("Search", "zoek");
        // assertTrue(searchC.isFormSubmission(request));
        //
        // sf.setName(col.getName());
        // sf.setStartAt(-1);
        // String japaneseStuff = "asml ã?®";
        // assertEquals(japaneseStuff.length(),6);
        // try {
        // assertEquals(URLEncoder.encode(japaneseStuff,
        // "UTF-8"),"asml+%E3%81%AE");
        // } catch (UnsupportedEncodingException e) {
        // fail(e.getMessage());
        // }
        // sf.setQuery(japaneseStuff);
        //
        // ModelAndView mv = searchC.onSubmit(request, response, sf, new
        // BindException(sf, "test"));
        //
        // assertEquals("returned correct view name", mv.getViewName(),
        // "search");
        // assertEquals("returned correct name", col.getName(), sf.getName());
        // log.debug("Query is: " + sf.getQuery());
        // assertEquals("returned correct query", japaneseStuff, sf.getQuery());
        // TODO test the results
    }
}
