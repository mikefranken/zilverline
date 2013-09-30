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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.SearchException;
import org.zilverline.core.SearchResult;
import org.zilverline.service.SearchServiceImpl;

/**
 * SearchController deals with handling form submission from search page. It's basically a SimpleFormController with reference data
 * containing the collections
 * 
 * @author Michael Franken
 * @version $Revision: 1.34 $
 */
public final class SearchController extends AbstractZilverController {

    public SearchController() {
        // need a session to hold the search attributes (collections, max, query, start) in form until they're changed
        // setSessionForm(true);
        // initialize form with defaults
        setBindOnNewForm(true);
    }

    private SearchServiceImpl service;

    /**
     * @return Returns the service.
     */
    public SearchServiceImpl getService() {
        return service;
    }

    /**
     * @param thatService The service to set.
     */
    public final void setService(final SearchServiceImpl thatService) {
        this.service = thatService;
    }

    /**
     * Check preconditions, collectionManager and service must exist.
     */
    public void afterPropertiesSet() {
        if (this.collectionManager == null) {
            throw new IllegalArgumentException("'collectionManager' is required");
        }
        if (this.service == null) {
            throw new IllegalArgumentException("'service' is required");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
     *      org.springframework.web.bind.ServletRequestDataBinder)
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(CollectionTriple[].class, "collections", new CustomCollectionEditor(collectionManager));
    }

    /**
     * Overridden method makes GETs behave like POSTSs.
     * 
     * <p>
     * see https://sourceforge.net/forum/forum.php?thread_id=938166&forum_id=250340 on how to use GET instead of posts
     * </p>
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#isFormSubmission(javax.servlet.http.HttpServletRequest)
     */
    protected boolean isFormSubmission(HttpServletRequest request) {
        // check presence of name="Search" request
        // if not there the view is not a result of a submitted form
        // so it must be the initial form, resulting from clicking the 'search'
        // in the menu or so
        return super.isFormSubmission(request) || (request.getParameter("Search") != null);
    }

    /**
     * Callback function that processes the form submission. It searches over all provided collections, and returns Results into
     * model.
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
        throws ServletException {
        SearchForm theForm = (SearchForm) command;
        String queryString = theForm.getQuery();
        int maxResults = theForm.getMaxResults();
        int startAt = theForm.getStartAt() - 1;
        try {
            CollectionTriple[] collections = theForm.getCollections();
            // get the names of selected collections
            String[] selectedNames = new String[] {};
            if (collections != null) {
                List selectedCollectionsList = new ArrayList();
                log.debug("Form mapped via CustomCollectionEditor with collections: " + collections + " of length "
                    + collections.length);
                for (int i = 0; i < collections.length; i++) {
                    if (collections[i].isSelected()) {
                        selectedCollectionsList.add(collections[i].getName());
                    }
                    log.debug(collections[i].getName() + ", " + collections[i].getId() + ", " + collections[i].isSelected());
                }
                selectedNames = (String[]) selectedCollectionsList.toArray(selectedNames);
            } else {
                setAllCollectionsSelected(theForm);
            }

            log.debug("Form called for collections '" + StringUtils.arrayToCommaDelimitedString(selectedNames) + "', with query '"
                + queryString + "', with maxResults '" + maxResults + "', starting at " + startAt);
            SearchResult result = new SearchResult(null, maxResults, startAt, startAt + maxResults);
            try {
                result = service.doSearch(selectedNames, queryString, startAt, maxResults);
            }
            catch (SearchException e) {
                log.error("Error executing query '" + queryString + "', " + e);
                errors.rejectValue("query", "errors.invalidquery", null, e.getLocalizedMessage());
            }
            // TODO SearchResult could be a map
            Map model = new HashMap();
            model.put("results", result.getResults());
            model.put("hits", new Integer(result.getNumberOfHits()));
            model.put("startAt", new Integer(result.getStartAt()));
            model.put("endAt", new Integer(result.getEndAt()));

            // create a ModelAndView that submits to original view
            // keeping the command, adding the model
            log.debug("returning to view: " + getSuccessView() + " with " + result.getNumberOfHits() + " hits");

            return showForm(request, errors, getSuccessView(), model);
        }
        catch (Exception e) {
            log.error("Error executing query '" + queryString + "', " + e);
            throw new ServletException("Error executing query '" + queryString + "'", e);
        }
    }

    /**
     * Get the initial values from the Service.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#onBindOnNewForm(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object)
     */
    protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {
        log.debug("Setting initial values from service in onBindOnNewForm");
        SearchForm theForm = (SearchForm) command;
        // this is a first request, and there are no collections in the (session) form, set all collections selected, and all
        // defaults
        theForm.setMaxResults(service.getMaxResults());
        theForm.setQuery(service.getQuery());
        setAllCollectionsSelected(theForm);
    }

    private void setAllCollectionsSelected(SearchForm theForm) {
        List theCollections = collectionManager.getCollections();
        CollectionTriple[] col3s = new CollectionTriple[theCollections.size()];
        int i = 0;
        for (Iterator iter = theCollections.iterator(); iter.hasNext();) {
            col3s[i++] = new CollectionTriple((DocumentCollection) iter.next(), true);
            log.debug("SearchController.onBindOnNewForm:Adding collection: " + col3s[i - 1].getName());
        }
        theForm.setCollections(col3s);
    }

}
