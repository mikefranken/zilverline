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
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.Handler;
import org.zilverline.core.IndexException;

/**
 * JavaBean form controller that is used to update the <code>IndexService</code> Extractor Mappings.
 * 
 * @author Michael Franken
 */
public class HandlersController extends AbstractZilverController {

    /**
     * Use service directly in form.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest arg0) throws Exception {
        return collectionManager.getArchiveHandler();
    }

    /** Method updates an existing SearchService. */
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
        throws ServletException {
        Handler thisForm = (Handler) command;

        // reconstruct the extractor map, it contains pairs of (extension,
        // extractor)
        // in the request they are related by the fact that the extension is in
        // a parameter with an integer value
        // and the extractor contains 'prefix' and corresponding integer value

        // first get the prefix (posted as hidden field)
        String prefix = request.getParameter("prefix");
        if (!StringUtils.hasLength(prefix)) {
            log.warn("no prefix set");
            prefix = "select_";
        }

        // get keys and values
        Map reqMap = request.getParameterMap();
        Iterator iter = reqMap.entrySet().iterator();
        String[] keys = new String[reqMap.size()];
        String[] values = new String[reqMap.size()];
        while (iter.hasNext()) {
            Map.Entry element = (Map.Entry) iter.next();
            String key = (String) element.getKey();
            String value = ((String[]) element.getValue())[0];
            log.debug("Parsing request for: " + key + ", " + value);
            try {
                if (key.startsWith(prefix)) {
                    String indexStr = key.substring(prefix.length());
                    int index = Integer.parseInt(indexStr);
                    log.debug("Adding " + value + " to values[" + index + "]");
                    values[index] = value;
                } else {
                    int index = Integer.parseInt(key);
                    log.debug("Adding " + value + " to keys[" + index + "]");
                    keys[index] = value;
                }
            }
            catch (NumberFormatException e) {
                // not an extractor related requestParameter
                log.debug("Skipping " + key + ", " + value);
            }
        }

        // add the key value pairs to Map, if key contains value
        Map props = new Properties();
        for (int i = 0; i < values.length; i++) {
            if (StringUtils.hasLength(keys[i])) {
                log.debug("Adding " + keys[i] + "," + values[i] + " to map");
                props.put(keys[i], values[i]);
            } else {
                log.debug("Skipping (remove) " + keys[i] + "," + values[i] + " to map");
            }
        }
        // set caseSensitivity before setting the map
        thisForm.setCaseSensitive(RequestUtils.getBooleanParameter(request, "casesensitive", false));
        // add the Map
        thisForm.setMappings(props);
        collectionManager.getArchiveHandler().setCaseSensitive(RequestUtils.getBooleanParameter(request, "casesensitive", false));
        collectionManager.getArchiveHandler().setMappings(props);
        try {
            collectionManager.store();
        }
        catch (IndexException e) {
            throw new ServletException("Error storing new Search Defaults", e);
        }

        return new ModelAndView(getSuccessView());
    }

}
