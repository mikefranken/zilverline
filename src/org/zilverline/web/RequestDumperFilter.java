/*
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zilverline.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Request filter that dumps interesting state information about a request to the associated servlet context log file, before
 * allowing the servlet to process the request in the usual way. This can be installed as needed to assist in debugging problems.
 * Used for debugging.
 * 
 * Modified version of the tomcat distribution now uses commons.logging.
 * 
 * @author Craig McClanahan
 * @author Michael Franken
 * @version $Revision: 1.4 $ $Date: 2005/12/01 21:53:51 $
 */

public final class RequestDumperFilter implements Filter {

    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(RequestDumperFilter.class);

    // ----------------------------------------------------- Instance Variables

    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not currently
     * configured.
     */
    private FilterConfig filterConfig = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.filterConfig = null;

    }

    /**
     * Time the processing that is performed by all subsequent filters in the current filter stack, including the ultimately invoked
     * servlet.
     * 
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     * 
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
        ServletException {

        if (filterConfig == null) {
            return;
        }

        log.debug("Request Received at " + (new Timestamp(System.currentTimeMillis())));
        log.debug(" characterEncoding=" + request.getCharacterEncoding());
        log.debug("     contentLength=" + request.getContentLength());
        log.debug("       contentType=" + request.getContentType());
        log.debug("            locale=" + request.getLocale());
        Enumeration locales = request.getLocales();
        StringBuffer localesBuffer = new StringBuffer("           locales=");
        boolean first = true;
        while (locales.hasMoreElements()) {
            Locale locale = (Locale) locales.nextElement();
            if (first) {
                first = false;
            } else {
                localesBuffer.append(", ");
            }
            localesBuffer.append(locale.toString());
        }
        log.debug(localesBuffer);
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            StringBuffer paramsBuffer = new StringBuffer();
            paramsBuffer.append("         parameter=" + name + "=");
            String[] values = request.getParameterValues(name);
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    paramsBuffer.append(", ");
                }
                paramsBuffer.append(values[i]);
            }
            log.debug(paramsBuffer);
        }
        log.debug("          protocol=" + request.getProtocol());
        log.debug("        remoteAddr=" + request.getRemoteAddr());
        log.debug("        remoteHost=" + request.getRemoteHost());
        log.debug("            scheme=" + request.getScheme());
        log.debug("        serverName=" + request.getServerName());
        log.debug("        serverPort=" + request.getServerPort());
        log.debug("          isSecure=" + request.isSecure());

        // Render the HTTP servlet request properties
        if (request instanceof HttpServletRequest) {
            log.debug("---------------------------------------------");
            HttpServletRequest hrequest = (HttpServletRequest) request;
            log.debug("       contextPath=" + hrequest.getContextPath());
            Cookie[] cookies = hrequest.getCookies();
            if (cookies == null) {
                cookies = new Cookie[0];
            }
            for (int i = 0; i < cookies.length; i++) {
                log.debug("            cookie=" + cookies[i].getName() + "=" + cookies[i].getValue());
            }
            names = hrequest.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                String value = hrequest.getHeader(name);
                log.debug("            header=" + name + "=" + value);
            }
            log.debug("            method=" + hrequest.getMethod());
            log.debug("          pathInfo=" + hrequest.getPathInfo());
            log.debug("       queryString=" + hrequest.getQueryString());
            log.debug("        remoteUser=" + hrequest.getRemoteUser());
            log.debug("requestedSessionId=" + hrequest.getRequestedSessionId());
            log.debug("        requestURI=" + hrequest.getRequestURI());
            log.debug("       servletPath=" + hrequest.getServletPath());
        }
        log.debug("=============================================");

        // Pass control on to the next filter
        chain.doFilter(request, response);

    }

    /**
     * Place this filter into service.
     * 
     * @param thisFilterConfig The filter configuration object
     */
    public void init(final FilterConfig thisFilterConfig) {
        this.filterConfig = thisFilterConfig;

    }

    /**
     * Return a String representation of this object.
     * 
     * @return String
     */
    public String toString() {

        if (filterConfig == null) {
            return ("RequestDumperFilter()");
        }
        StringBuffer sb = new StringBuffer("RequestDumperFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

}
