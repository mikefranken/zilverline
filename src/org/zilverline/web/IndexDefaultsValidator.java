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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.zilverline.service.CollectionManager;

/**
 * JavaBean <code>Validator</code> for <code>IndexDefaultsController</code>.
 * 
 * @author Michael Franken
 * @version $Revision: 1.2 $
 */
public class IndexDefaultsValidator implements Validator {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(IndexDefaultsValidator.class);

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return CollectionManager.class.isAssignableFrom(clazz);
    }

    /**
     * Validator for CollectionManager. Validates the part on the indexDefaultsController: priority, mergeFactor, etc.
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        CollectionManager manager = (CollectionManager) obj;
        if (manager.getPriority() != null) {
            if (manager.getPriority().intValue() < 1 || manager.getPriority().intValue() > 10) {
                errors.rejectValue("priority", null, new Object[] { manager.getPriority() },
                    "must be a positive number between 1 and 10.");
            }
        }
        if (manager.getMergeFactor() != null) {
            if (manager.getMergeFactor().intValue() < 0) {
                errors.rejectValue("mergeFactor", "error.notapositivenumber", new Object[] { manager.getMergeFactor() },
                    "must be a positive number.");
            }
        }
        if (manager.getMaxMergeDocs() != null) {
            if (manager.getMaxMergeDocs().intValue() < 0) {
                errors.rejectValue("maxMergeDocs", "error.notapositivenumber", new Object[] { manager.getMaxMergeDocs() },
                    "must be a positive number.");
            }
        }
        if (manager.getMinMergeDocs() != null) {
            if (manager.getMinMergeDocs().intValue() < 0) {
                errors.rejectValue("minMergeDocs", "error.notapositivenumber", new Object[] { manager.getMinMergeDocs() },
                    "must be a positive number.");
            }
        }
    }
}
