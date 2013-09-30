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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.IndexException;

/**
 * JavaBean form controller that is used to add a new <code>Collection</code> to the system.
 * 
 * @author Michael Franken
 */
public class AddCollectionController extends AbstractZilverController {

    public AddCollectionController() {
        // activate session form mode to allow for detection of duplicate submissions
        setSessionForm(true);
    }

    /** Method inserts a new <code>Collection</code>. */
    protected ModelAndView onSubmit(Object command) throws ServletException {
        DocumentCollection collection = (DocumentCollection) command;
        // delegate the insert to the Collection Manager
        try {
            collectionManager.addCollection(collection);
            collection.init();
            collectionManager.store();
        }
        catch (IndexException e) {
            throw new ServletException("Error initializing new index in AddCollectionForm", e);
        }
        return new ModelAndView(getSuccessView(), "collections", collectionManager.getCollections());
    }

    protected ModelAndView handleInvalidSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return disallowDuplicateFormSubmission(request, response);
    }

}
