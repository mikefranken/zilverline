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

package org.zilverline.lucene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents boosting factors per Field.
 * 
 * These are factors used by zilverline to boost a query. By default a given query such as 'java "method invocation" rmi' looks for
 * these terms in the contents of a document. By setting boost factors for other attributes of a document, such as title, name, and
 * summary you can tune the result of a query. Normally you start with contents (boostfactor 1.0), and then specify that you want a
 * occurence of the query terms within the name of a document to be twice as important as a hit within the contents, you specify a
 * boostfactor of 2.0. The example below rates an occurrence of query terms within summary to be three times as important as within
 * contents, and the title four times. These are typically read from Spring configuration:
 * 
 * <pre>
 *    
 *     
 *      
 *               &lt;bean id=&quot;boostFactor&quot; class=&quot;org.zilverline.lucene.BoostFactor&quot;&gt;
 *                   &lt;property name=&quot;factors&quot;&gt;
 *                        &lt;props&gt;
 *                            &lt;prop key=&quot;contents&quot;&gt;1.0&lt;/prop&gt;
 *                            &lt;prop key=&quot;name&quot;&gt;2&lt;/prop&gt;
 *                            &lt;prop key=&quot;summary&quot;&gt;3&lt;/prop&gt;
 *                            &lt;prop key=&quot;title&quot;&gt;4&lt;/prop&gt;
 *                        &lt;/props&gt;
 *                    &lt;/property&gt;
 *                &lt;/bean&gt;
 *            
 *      
 *     
 * </pre>
 * 
 * @author Michael Franken
 * @version $Revision: 1.13 $
 */
public class BoostFactor {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(BoostFactor.class);

    /** Map holding field names with to boostfactors. */
    private Map factors = new HashMap();

    /**
     * Constructs BoostFactor with defualt boosts. For testing purposes and backward compatibility, overwritten be external
     * configuration, if any
     */
    public BoostFactor() {
        factors.put("contents", new Float(1.0));
        factors.put("title", new Float(3.0));
        factors.put("summary", new Float(2.0));
        factors.put("name", new Float(4.0));
    }

    /**
     * Set Factors from a Map object. The Factors Fields with commands as values. For instance 'content=1.0'.
     * 
     * @param props properties as a Map with extension as key and command as value
     */
    public final void setFactors(final Map props) {
        factors.clear();
        // convert the keys to lowercase
        Iterator iter = props.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry element = (Map.Entry) iter.next();
            factors.put(((String) element.getKey()).toLowerCase(), Float.valueOf((String) element.getValue()));
        }
    }

    /**
     * Get the Factors for the Fields.
     * 
     * @return the Factors
     */
    public final Map getFactors() {
        return factors;
    }
}
