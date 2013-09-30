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

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import org.zilverline.core.FileSystemCollection;

/**
 * <code>Validator</code> for <code>Collection</code> forms.
 * 
 * @author Michael Franken
 */
public class CollectionValidator implements Validator {

    public boolean supports(Class clazz) {
        return FileSystemCollection.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        FileSystemCollection collection = (FileSystemCollection) obj;
        ValidationUtils.rejectIfEmpty(errors, "name", "error.required", "required");
        ValidationUtils.rejectIfEmpty(errors, "contentDir", "error.required", "required");
        File thisFile = (File) errors.getFieldValue("contentDir");
        if (thisFile == null || !thisFile.isDirectory()) {
            errors.rejectValue("contentDir", "error.dirnoexist", "directory does not exist");
        }

    }

}
