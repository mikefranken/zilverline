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

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.service.IndexService;

/**
 * Controller to extraction process. Takes a DocumentFromCacheForm as input.
 * 
 * @author Michael Franken
 * @version $Revision: 1.18 $
 */
public class CacheController extends AbstractZilverController {
    private IndexService indexService;

    /**
     * @return Returns the indexService.
     */
    public IndexService getIndexService() {
        return indexService;
    }

    /**
     * @param thatService The indexService to set.
     */
    public final void setIndexService(final IndexService thatService) {
        this.indexService = thatService;
    }

    /**
     * Handles form submission by extracting the corresponding archive.
     * 
     * @param command the DocumentFromCacheForm, containing document information.
     * 
     * @return ModelAndView the view with updated model
     * 
     * @throws ServletException on error
     */
    public ModelAndView onSubmit(Object command) throws ServletException {
        DocumentFromCacheForm df = (DocumentFromCacheForm) command;
        String colName = df.getCollection();

        log.debug("CacheController for " + colName);

        FileSystemCollection thisCollection = (FileSystemCollection) collectionManager.getCollectionByName(colName);
        boolean canForward = false;

        try {
            if (thisCollection != null) {
                if (!thisCollection.getArchiveCache().contains(df.getArchive())) {
                    // not already in cache for this collection
                    log.debug("CacheController cache miss for " + colName);

                    // but the file might be there from a previous time, before
                    // possible restart
                    File f = new File(thisCollection.getCacheDirWithManagerDefaults(), df.getFile());

                    if (f.exists()) {
                        log.debug("CacheController cache from previous life for " + f);
                        canForward = true;

                        // the file must be from archive, add the zip to the
                        // cache, without having extracted it.
                        // this is a risk, since the some files from the archive
                        // in cache may have been deleted
                        thisCollection.getArchiveCache().add(df.getArchive());
                    } else {
                        File zipFile = new File(thisCollection.getContentDir(), df.getArchive());

                        if (collectionManager.expandArchive(thisCollection, zipFile)) {
                            thisCollection.getArchiveCache().add(df.getArchive());
                            canForward = true;
                        } else {
                            log.debug("CacheController can't get file from archive for: " + zipFile);
                        }
                    }
                } else {
                    log.debug("CacheController cache hit for " + colName);
                    canForward = true;
                }
            } else {
                log.error("no valid collection");
            }
        }
        catch (IndexException e) {
            throw new ServletException(e);
        }

        if (canForward) {
            // the archive has been extracted, redirect to file
            log.debug("redirecting from CacheController to:  " + thisCollection.getCacheUrlWithManagerDefaults() + df.getFile());

            RedirectView v = new RedirectView();

            v.setUrl(thisCollection.getCacheUrlWithManagerDefaults() + df.getFile());

            return new ModelAndView(v);
        }

        log.debug("returning from CacheController to view:  " + getSuccessView() + " with " + colName);

        return new ModelAndView(getSuccessView(), "document", df);
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
        return true;
    }

}
