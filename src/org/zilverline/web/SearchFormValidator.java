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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * JavaBean <code>Validator</code> for <code>SearchForm</code>.
 * 
 * @author Michael Franken
 * @version $Revision: 1.17 $
 */
public class SearchFormValidator implements Validator {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(SearchFormValidator.class);

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return SearchForm.class.isAssignableFrom(clazz);
    }

    /**
     * Validator for SearchForm. Validates name, maxResults, startAt and query
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        SearchForm search = (SearchForm) obj;
        String query = search.getQuery();
        int maxResults = search.getMaxResults();
        int startAt = search.getStartAt();

        if (search.getCollections() != null) {
            if (search.getCollections().length <= 0) {
                log.debug("Validation rejected since there are no collections");
                errors.rejectValue("maxResults", "error.nocollections", null, "there are no collections");
                return;
            }
        }

        if (maxResults <= 0) {
            log.debug("Validation rejected for maxResults " + maxResults);
            errors.rejectValue("maxResults", "error.notapositivenumber", new Object[] { new Integer(maxResults) },
                "must be a positive number.");
        }

        if (startAt <= 0) {
            search.setStartAt(1);
        }

        if (!StringUtils.hasText(query)) {
            log.debug("Validation rejected for empty query ");
            errors.rejectValue("query", "error.emptyquery", null, "query can not be empty");
        }

    }
}
