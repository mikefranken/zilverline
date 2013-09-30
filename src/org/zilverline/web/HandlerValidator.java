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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.zilverline.core.Handler;
import org.zilverline.util.StreamGobbler;
import org.zilverline.util.SysUtils;

/**
 * JavaBean <code>Validator</code> for <code>SearchForm</code>.
 * 
 * @author Michael Franken
 * @version $Revision: 1.4 $
 */
public class HandlerValidator implements Validator {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(HandlerValidator.class);

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return Handler.class.isAssignableFrom(clazz);
    }

    /**
     * Validator for SearchForm. Validates name, maxResults, startAt and query
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        Handler handler = (Handler) obj;
        Map mappings = handler.getMappings();
        // convert the keys to lowercase
        Iterator mapping = mappings.entrySet().iterator();

        try {
            while (mapping.hasNext()) {
                Map.Entry element = (Map.Entry) mapping.next();
                String archiver = (String) element.getValue();
                log.debug("checking mappings: " + archiver);
                // can be empty: then java.util.Zip used
                if (StringUtils.hasLength(archiver)) {
                    // the archiver is an external application with options,
                    // check whether the application exists
                    String exe = archiver.split(" ")[0];
                    log.debug("checking mappings: " + exe);
                    File exeFile = new File(exe);
                    if (exeFile.exists()) {
                        log.debug("Can find " + exe);
                        continue;
                    }

                    // else try find the thing on the path
                    Process proc = Runtime.getRuntime().exec(exe);
                    // any error message?
                    StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
                    // any output?
                    StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                    // kick them off
                    errorGobbler.start();
                    outputGobbler.start();

                    proc.destroy();
                    log.debug("Exit value: " + proc.exitValue());

                    // everthing OK?
                    if (proc.exitValue() != 0) {
                        // error executing proc
                        log.debug(" --> Can't execute: '" + exe + "'. Exit value: " + SysUtils.getErrorTextById(proc.exitValue()));
                        log.debug("mappings must exist on disk: " + exe);
                        errors.rejectValue("mappings", null, null, "must exist on disk.");
                    } else {
                        log.debug(" --> Can execute: '" + exe + "'. Exit value: " + SysUtils.getErrorTextById(proc.exitValue()));
                    }
                }
            }
        }
        catch (Exception e) {
            log.debug("Can not execute one of the mappings", e);
            errors.rejectValue("mappings", null, null, "must exist on disk.");
        }

    }
}
