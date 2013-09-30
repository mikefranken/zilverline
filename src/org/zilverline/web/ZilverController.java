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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.service.CollectionManager;
import org.zilverline.util.FileUtils;

/**
 * <code>MultiActionController</code> that handles all non-form URL's.
 * 
 * @author Michael Franken
 * @version $Revision: 1.22 $
 */
public final class ZilverController extends MultiActionController implements InitializingBean {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(ZilverController.class);

    /** CollectionManager holds all collections. */
    private CollectionManager collectionManager;

    /**
     * Set the collectionManager.
     * 
     * @param cm the CollectionManager
     */
    public void setCollectionManager(CollectionManager cm) {
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
     * Checks whether we have a collectionManager.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        if (collectionManager == null) {
            throw new ApplicationContextException("Must set collectionManager bean property on " + getClass());
        }
    }

    // handlers

    /**
     * Custom handler for collections display.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * 
     * @return a ModelAndView to render the response
     * 
     * @throws ServletException on error
     */
    public ModelAndView collectionsHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.debug("returning 'collections' view");

        return new ModelAndView("collections", "collections", collectionManager.getCollections());
    }

    /**
     * Custom handler for log4j display.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * 
     * @return a ModelAndView to render the response
     * 
     * @throws ServletException on error
     */
    public ModelAndView log4JHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.debug("returning 'log4J' view");

        return new ModelAndView("log4j");
    }

    /**
     * Custom handler for removal of volatile cache.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * 
     * @return a ModelAndView to render the response
     * 
     * @throws ServletException on error
     */
    public ModelAndView flushCacheHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String collectionName = request.getParameter("collection");

        log.debug("Request to flush cache for collection: " + collectionName);

        FileSystemCollection thisCollection = (FileSystemCollection) collectionManager.getCollectionByName(collectionName);

        // only flush 'volatile' caches
        if ((thisCollection != null) && !thisCollection.isKeepCacheWithManagerDefaults()) {
            FileUtils.removeDir(thisCollection.getCacheDirWithManagerDefaults());
            log.debug("Flushed cache for collection: " + collectionName);
            thisCollection.getCacheDirWithManagerDefaults().mkdirs();
            thisCollection.getArchiveCache().clear();
        } else {
            throw new ServletException("Can not flush cache for collection: " + collectionName);
        }

        return new ModelAndView("collections", "collections", collectionManager.getCollections());
    }

    /**
     * Custom handler for errors display.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * 
     * @return a ModelAndView to render the response
     * 
     * @throws ServletException on error
     */
    public ModelAndView errorsHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.warn("returning 'error' view");

        // Reinitialize all collections for safety sake. The index may have been
        // update while an error occurred
        try {
            collectionManager.init();
        }
        catch (IndexException e) {
            log.error("Can't reinitialize index after error");
        }

        return new ModelAndView("error");
    }
}
