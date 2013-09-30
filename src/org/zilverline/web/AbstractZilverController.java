/*
 * Copyright 2003-20054 Michael Franken, Zilverline.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.CancellableFormController;

import org.zilverline.service.CollectionManager;

/**
 * JavaBean abstract base class for CollectionManager-aware form controllers. Provides convenience methods for subclasses.
 * 
 * @author Michael Franken
 */
public abstract class AbstractZilverController extends CancellableFormController {

    /**
     * logger for Commons logging. This is non-static final protected, such that it defines a log for all subclasses too.
     */
    protected final Log log = LogFactory.getLog(getClass().getName());

    protected CollectionManager collectionManager;

    /**
     * Set the collectionManager.
     * 
     * @param cm the CollectionManager
     */
    public void setCollectionManager(final CollectionManager cm) {
        collectionManager = cm;
    }

    /**
     * Get the collectionManager.
     * 
     * @return CollectionManager the collectionManager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Check preconditions, collectionManager must exist.
     */
    public void afterPropertiesSet() {
        if (this.collectionManager == null) {
            throw new IllegalArgumentException("'collectionManager' is required");
        }
    }

    /**
     * Callback Method disallows duplicate form submission. Typically used to prevent duplicate insertion of entities into the
     * datastore. Shows a new form with an error message.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return model and view with the error message
     * @throws Exception
     */
    protected ModelAndView disallowDuplicateFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        BindException errors = getErrorsForNewForm(request);
        errors.reject("duplicateFormSubmission", "Duplicate form submission");
        return showForm(request, response, errors);
    }

}
