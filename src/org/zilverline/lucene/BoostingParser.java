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

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

/**
 * BoostingParser extends QueryParser by boosting its terms.
 */
public class BoostingParser extends QueryParser {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(BoostingParser.class);

    private String defaultField = "contents";

    private BoostFactor factors;

    /**
     * @param factors The factors to set.
     */
    public void setFactors(BoostFactor factors) {
        this.factors = factors;
    }

    public BoostingParser(String f, Analyzer a) {
        super(f, a);
        defaultField = f;
    }

    /**
     * Callback that returns Query with boosted fields using BoostFactors
     * 
     * @param field the field to query
     * @param analyzer the analyzer to use
     * @param queryText the query
     * 
     * @return Query object
     * 
     * @throws ParseException if Query can't be made
     * 
     */
    protected Query getFieldQuery(String field, Analyzer analyzer, String queryText) throws ParseException {
        // Use the analyzer to get all the tokens, and then build a TermQuery,
        // PhraseQuery, or nothing based on the term count
        // for field that contain 'contents' add boostfactors for other terms
        // specified in BoostFactor
        if (factors != null && factors.getFactors() != null && !factors.getFactors().isEmpty() && defaultField.equals(field)) {
            TokenStream source = analyzer.tokenStream(field, new StringReader(queryText));
            Vector v = new Vector();
            org.apache.lucene.analysis.Token t;

            while (true) {
                try {
                    t = source.next();
                }
                catch (IOException e) {
                    t = null;
                }
                if (t == null) {
                    break;
                }
                v.addElement(t.termText());
                log.debug(field + " , " + t.termText());
            }

            try {
                source.close();
            }
            catch (IOException e) {
                log.error("Unexpected Exception");
            }

            if (v.size() == 0) {
                return null;
            } else {
                // create a new composed query
                BooleanQuery bq = new BooleanQuery();

                // For all boostfactors create a new PhraseQuery
                Iterator iter = factors.getFactors().entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry element = (Map.Entry) iter.next();
                    String thisField = ((String) element.getKey()).toLowerCase();
                    Float boost = (Float) element.getValue();
                    PhraseQuery q = new PhraseQuery();

                    // and add all the terms of the query
                    for (int i = 0; i < v.size(); i++) {
                        q.add(new Term(thisField, (String) v.elementAt(i)));
                    }

                    // boost the query
                    q.setBoost(boost.floatValue());

                    // and add it to the composed query
                    bq.add(q, false, false);
                }
                log.debug("Query: " + bq);

                return bq;
            }
        } else {
            log.debug("Treat like normal query: " + queryText);
            return super.getFieldQuery(field, analyzer, queryText);
        }
    }

}
