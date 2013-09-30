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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.SearchException;
import org.zilverline.lucene.BoostFactor;
import org.zilverline.service.SearchServiceImpl;

/**
 * JavaBean form controller that is used to update the <code>SearchService</code> Defaults.
 * 
 * @author Michael Franken
 */
public class SearchDefaultsController extends AbstractZilverController {

    private SearchServiceImpl service;

    /**
     * Use service directly in form.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest arg0) throws Exception {
        return service;
    }

    /**
     * Check precondition: service must exist.
     */
    public void afterPropertiesSet() {
        if (this.service == null) {
            throw new IllegalArgumentException("'a SearchService' is required");
        }
    }

    /**
     * Callback function for reference data: the number of the Boostable fields.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#referenceData(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object, org.springframework.validation.Errors)
     */
    protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
        Map model = new HashMap();
        model.put("allBoostableFieldsSize", new Integer(service.getAllBoostableFields().length));
        return model;
    }

    /**
     * Method updates an existing SearchService.
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
        throws ServletException {
        // get keys and values for BoostFactors
        Map reqMap = request.getParameterMap();
        Iterator iter = reqMap.entrySet().iterator();
        String[] values = new String[reqMap.size()];
        String[] fields = new String[reqMap.size()];
        String prefix = "field_";
        // find corresponding field, value pairs, they have matching numbers as
        // parameter names (field_#,#) (# is a number)
        while (iter.hasNext()) {
            Map.Entry element = (Map.Entry) iter.next();
            String key = (String) element.getKey();
            String value = ((String[]) element.getValue())[0];
            log.debug("Parsing request for: " + key + ", " + value);
            try {
                if (key.startsWith(prefix)) {
                    // this is a field named field_#
                    String indexStr = key.substring(prefix.length());
                    int index = Integer.parseInt(indexStr);
                    log.debug("Adding " + value + " to fields[" + index + "]");
                    fields[index] = value;
                } else {
                    // this could be a value named #
                    int index = Integer.parseInt(key);
                    log.debug("Adding " + value + " to values[" + index + "]");
                    values[index] = value;
                }
            }
            catch (NumberFormatException e) {
                // not an extractor related requestParameter
                log.debug("Skipping " + key + ", " + value);
            }
        }
        // add the key value pairs to Map, if field and value contains value
        Map props = new Properties();
        for (int i = 0; i < fields.length; i++) {
            if (StringUtils.hasLength(fields[i]) && StringUtils.hasLength(values[i])) {
                log.debug("Adding " + fields[i] + "=" + values[i] + " to map");
                props.put(fields[i], values[i]);
                // validate the value
                try {
                    float f = Float.parseFloat(values[i]);
                }
                catch (NumberFormatException e) {
                    log.debug("value must be a float: " + values[i]);
                    errors.rejectValue("factors", "error.notapositivenumber", null, "must be a float.");
                    // TODO this is not great, and it does not show error.
                    // OnBindAndValidate?
                    return new ModelAndView(getSuccessView());
                }

            } else {
                log.debug("Skipping (remove) " + fields[i] + "=" + values[i] + " from map");
            }
        }
        if (service.getFactors() != null) {
            service.getFactors().setFactors(props);
        } else {
            // TODO: this is funny,and should not occur, prevent this from happening somewhere else
            log.warn("SearchService should have Boostfactors, creating new");
            service.setFactors(new BoostFactor());
            service.getFactors().setFactors(props);
        }

        try {
            service.store();
        }
        catch (SearchException e) {
            throw new ServletException("Error storing new Search Defaults", e);
        }
        return new ModelAndView(getSuccessView());
    }

    /**
     * @param thisService The service to set.
     */
    public final void setService(final SearchServiceImpl thisService) {
        this.service = thisService;
    }

    /**
     * @return Returns the service.
     */
    public final SearchServiceImpl getService() {
        return service;
    }
}
