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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.zilverline.lucene.BoostFactor;
import org.zilverline.service.SearchServiceImpl;

/**
 * JavaBean <code>Validator</code> for <code>SearchForm</code>.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public class SearchServiceValidator implements Validator {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(SearchServiceValidator.class);

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return SearchServiceImpl.class.isAssignableFrom(clazz);
    }

    /**
     * Validator for SearchForm. Validates name, maxResults, startAt and query
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        SearchServiceImpl service = (SearchServiceImpl) obj;
        int maxResults = service.getMaxResults();

        if (maxResults <= 0) {
            log.debug("Validation rejected for maxResults " + maxResults);
            errors.rejectValue("maxResults", "error.notapositivenumber", new Object[] { new Integer(maxResults) },
                "must be a positive number.");
        }

        BoostFactor factors = service.getFactors();
        Map mappings = factors.getFactors();
        // convert the keys to lowercase
        Iterator iter = mappings.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry element = (Map.Entry) iter.next();
            String floatString = (String) element.getValue();
            log.debug("checking mappings: " + floatString);
            try {
                float f = Float.parseFloat(floatString);
            }
            catch (NumberFormatException e) {
                log.debug("value must be a float: " + floatString);
                errors.rejectValue("factors", null, null, "must be a float.");
            }
        }
    }
}
