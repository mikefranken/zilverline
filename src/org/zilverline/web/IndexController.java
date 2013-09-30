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

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.IndexException;
import org.zilverline.service.IndexService;

/**
 * Controller to indexing process. Takes a CollectionForm as input.
 * 
 * @author Michael Franken
 * @version $Revision: 1.16 $
 */
public class IndexController extends AbstractZilverController {

    IndexService indexService;

    /**
     * Handles form submission by indexing given collections.
     * 
     * @param command the CollectionForm
     * 
     * @return ModelAndView the view with updated model
     * 
     * @throws ServletException on Indexing error
     */
    public ModelAndView onSubmit(Object command) throws ServletException {
        String[] colNames = ((CollectionForm) command).getNames();
        boolean fullIndex = ((CollectionForm) command).getFullIndex();
        String cols = StringUtils.arrayToCommaDelimitedString(colNames);

        log.debug("Form called with " + cols + ", reindexing: " + fullIndex);

        try {
            indexService.doIndex(colNames, fullIndex);
        }
        catch (IndexException e) {
            throw new ServletException("Can't index collections " + cols, e);
        }

        log.debug("returning from CollectionForm to view:  " + getSuccessView() + " with " + cols);

        return new ModelAndView(getSuccessView(), "collections", getCollectionManager().getCollections());
    }

    /**
     * @return Returns the indexService.
     */
    public IndexService getIndexService() {
        return indexService;
    }

    /**
     * @param indexService The indexService to set.
     */
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.zilverline.web.AbstractZilverController#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        if (this.collectionManager == null) {
            throw new IllegalArgumentException("'collectionManager' is required");
        }
        if (this.indexService == null) {
            throw new IllegalArgumentException("'indexService' is required");
        }
    }
}
