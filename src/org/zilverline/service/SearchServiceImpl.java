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
package org.zilverline.service;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;
import org.zilverline.core.Result;
import org.zilverline.core.SearchException;
import org.zilverline.core.SearchResult;
import org.zilverline.dao.DAOException;
import org.zilverline.dao.SearchServiceDAO;
import org.zilverline.lucene.BoostFactor;
import org.zilverline.lucene.BoostingParser;

/**
 * Service that searches collections.
 * 
 * @author Michael Franken
 * @version $Revision: 1.8 $
 */
public final class SearchServiceImpl implements SearchService {

    /** Access to collections, Populated through configuration. */
    private transient CollectionManager collectionManager;

    /** DAO for persisting SearchService. */
    private transient SearchServiceDAO dao;

    /** Array containing all boostable Fields. */
    private transient String[] allBoostableFields = new String[] { "contents", "name", "summary", "title" };

    /** logger for Commons logging. */
    private transient Log log = LogFactory.getLog(SearchServiceImpl.class);

    /** The default value for the number of results returned by a query. */
    private int maxResults = 20;

    /** The default search query. */
    private String query = "";

    /** Factors by which fields are boosted. */
    private BoostFactor factors = new BoostFactor();

    /**
     * @return Returns the factors.
     */
    public BoostFactor getFactors() {
        return factors;
    }

    /**
     * @param thoseFactors The factors to set.
     */
    public void setFactors(final BoostFactor thoseFactors) {
        this.factors = thoseFactors;
    }

    /**
     * Gets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @return CollectionManager
     * 
     * @see CollectionManager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * @return Returns the dao.
     */
    public SearchServiceDAO getDao() {
        return dao;
    }

    /**
     * Get the maximum number of results returned by a query.
     * 
     * @return the number of results
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Gets the default search query.
     * 
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Initializes the SearchService by reading defaults from DAO.
     * 
     */
    public void init() {
        SearchServiceImpl tempService = dao.load();
        if (tempService != null) {
            maxResults = tempService.getMaxResults();
            query = tempService.getQuery();
            factors = tempService.getFactors();
        } else {
            if (factors == null) {
                factors = new BoostFactor();
            }
        }
    }

    /**
     * Sets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @param cm CollectionManager
     * 
     * @see CollectionManager
     */
    public void setCollectionManager(final CollectionManager cm) {
        collectionManager = cm;
    }

    /**
     * @param thisDao The dao to set.
     */
    public void setDao(final SearchServiceDAO thisDao) {
        this.dao = thisDao;
    }

    /**
     * Set the maximum number of results returned by a query.
     * 
     * @param max value for the number of results.
     */
    public void setMaxResults(final int max) {
        maxResults = max;
    }

    /**
     * Set the default query.
     * 
     * @param thisQuery the default search query.
     */
    public void setQuery(final String thisQuery) {
        query = thisQuery;
    }

    /**
     * Store the SearchService to store.
     * 
     * @throws SearchException on error
     */
    public void store() throws SearchException {
        if (dao != null) {
            try {
                dao.store(this);
            }
            catch (DAOException e) {
                throw new SearchException("Can not save collections", e);
            }
        } else {
            log.error("No DAO set for SearchService");
        }
    }

    /**
     * Searches the given Collections for the query. Sending an empty or null array of names will betreated as if all collections
     * need to be searched, which is handy for external queries, that can't know the names of collections.
     * 
     * @param names array of collection names
     * @param queryString the query
     * @param startAt the first result to return (start counting from 0)
     * @param numberOfResults the (maximum) number of results to return
     * @return Object containing the results, and number of hits and the possibly changed startAt and endAt
     * 
     * @throws SearchException if query can't be executed
     */
    public SearchResult doSearch(final String[] names, final String queryString, int startAt, final int numberOfResults)
        throws SearchException {
        try {
            MultiSearcher ms = null;
            try {
                // for given collections create a List of IndexSearchers
                IndexSearcher[] allSearchers = createSearchersForCollectionsByName(names);

                // prepare the query
                // TODO: which analyzer to use? Different collections may have different Analyers
                Analyzer analyzer = getCollectionManager().createAnalyzer();
                log.debug("Using Analyzer " + analyzer.getClass());

                // for each occurence of contents, add a boost for fields specified in BoostFactor
                BoostingParser zp = new BoostingParser("contents", analyzer);
                zp.setFactors(factors);

                Query localquery = zp.parse(queryString);
                log.debug("the Query: " + query);

                // and search in all collections
                ms = new MultiSearcher(allSearchers);
                Hits hits = ms.search(localquery);

                log.debug("Query " + queryString + ", with " + hits.length() + " hits");
                // if we're changing the query, and we've paged too far
                if (startAt >= hits.length() || startAt < 0) {
                    startAt = 0;
                }
                // only get the number of results we're interested in that is from startAt to endAt, or last
                int endAt = startAt + numberOfResults;
                // set the max index to maxpage or last
                if (endAt > hits.length()) {
                    endAt = hits.length();
                }
                log.debug("Returning hits " + startAt + " to " + (endAt - 1));

                // get all the hits into results array
                Result[] results = new Result[endAt - startAt];

                Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>"),
                    new QueryScorer(localquery));

                // get all the hits into Result
                for (int j = startAt; j < endAt; j++) {
                    Document doc = hits.doc(j);
                    float score = hits.score(j);
                    results[j - startAt] = doc2ResultHelper(doc, score, highlighter, analyzer);
                }
                log.info("The query '" + queryString + "', has " + hits.length() + " hits, returning " + maxResults + " results: "
                    + (startAt + 1) + " to " + endAt);

                return new SearchResult(results, hits.length(), startAt, endAt);
            }
            catch (org.apache.lucene.queryParser.ParseException pe) {
                throw new SearchException("Error executing query '" + queryString + "'", pe);
            }
            catch (TooManyClauses e) {
                throw new SearchException("Error executing query '" + queryString
                    + ". Too complex, possibly spanning more than 1024 days'", e);
            }
            finally {
                if (ms != null) {
                    ms.close();
                }
            }
        }
        catch (Exception e) {
            log.error("Error executing query '" + queryString + "', " + e);
            throw new SearchException("Error executing query '" + queryString + "'", e);
        }
    }

    /**
     * Create IndexSearchers for all given document collections.
     * 
     * @param names the names of collections
     * @return Array of IndexSearchers based on given collection names
     * @throws IndexException
     * @throws IOException
     */
    private IndexSearcher[] createSearchersForCollectionsByName(final String[] names) throws IndexException, IOException {
        // create an array of searchers, one searcher per valid
        // collection this will be argument to MultiSearcher
        List allSearchersList = new ArrayList();
        // if any collection specified, find it by name using the
        // CollectionManager
        if (names != null && names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                String colName = names[i];
                DocumentCollection thisCollection = collectionManager.getCollectionByName(colName);
                if (thisCollection != null) {
                    addCollectionIfValidToSearchers(thisCollection, allSearchersList);
                } else {
                    log.warn("Invalid collection '" + colName + "' specified, skipping");
                }
            }
        } else {
            // no collection specified, use all
            log.warn("No collections specified, assuming you want all");
            List allCollections = collectionManager.getCollections();
            Iterator colIt = allCollections.iterator();
            while (colIt.hasNext()) {
                FileSystemCollection thisCollection = (FileSystemCollection) colIt.next();
                addCollectionIfValidToSearchers(thisCollection, allSearchersList);
            }
        }

        IndexSearcher[] allSearchers = new IndexSearcher[allSearchersList.size()];

        for (int j = 0; j < allSearchersList.size(); j++) {
            allSearchers[j] = (IndexSearcher) allSearchersList.get(j);
        }

        return allSearchers;
    }

    private void addCollectionIfValidToSearchers(DocumentCollection thisCollection, List allSearchersList) throws IndexException,
        IOException {
        if (thisCollection.isIndexValid()) {
            if (thisCollection.getIndexDirWithManagerDefaults() != null) {
                // create IndexSearcher with String argument,
                // not IndexReader, as reader will stay open
                allSearchersList.add(new IndexSearcher(thisCollection.getIndexDirWithManagerDefaults().toString()));
                log.info("Searching in collection: " + thisCollection.getName());
            }
        } else {
            log.warn("Skipping possibly invalid collection '" + thisCollection.getName() + "'.");
        }
    }

    /**
     * Helper that creates a Result from a Document.
     * 
     * @param doc the Document
     * @param score the score of the Document in the hit
     * @param hl the Highlighter used
     * @param an the Analyzer used
     * 
     * @return Result the resulting object that is used in the model.
     */
    private Result doc2ResultHelper(final Document doc, final float score, final Highlighter hl, final Analyzer an) {
        String docTitle = doc.get("title");
        String docName = doc.get("name");
        String docPath = doc.get("path");
        String zipName = doc.get("zipName");
        if (log.isDebugEnabled()) {
            log.debug("Preparing result " + docName + ":" + zipName);
        }
        String zipPath = doc.get("zipPath");
        String docURL = "";
        String docCache = "";
        String docCollection = doc.get("collection");

        // get the collection
        DocumentCollection thisCollection = collectionManager.getCollectionByName(docCollection);

        if (thisCollection != null) {
            docURL = thisCollection.getUrlDefault();

            if (thisCollection.isKeepCacheWithManagerDefaults()) {
                docCache = thisCollection.getCacheUrlWithManagerDefaults();
            }
        } else {
            log.error("Unknown collection '" + docCollection + "' found, can not find its URL.");
        }

        // get the modification date, and convert it to a readable form date is stored as (yyyyMMdd)
        DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
        Date docDate = null;

        try {
            docDate = df1.parse(doc.get("modified"));
        }
        catch (ParseException e) {
            log.debug("Invalid date retrieved, trying backward compatibility with v 1.0-rc3-patch1");

            // backward compatibility with v 1.0-rc3-patch1 storage of date:
            // Keyword<modified:0cee68g00>
            docDate = DateField.stringToDate(doc.get("modified"));
            if (docDate == null) {
                log.warn("Invalid date retrieved, returning epoch (1970) for " + docName);
                docDate = new Date(0);
            }
        }

        String docSize = doc.get("size");
        String docType = doc.get("type");
        String docISBN = doc.get("isbn");

        // use the name if it has no title
        // TODO this logic could go into Result
        if ((docTitle == null) || docTitle.equals("")) {
            if ((zipName == null) || zipName.equals("")) {
                docTitle = docName;
            } else {
                docTitle = zipName;
            }
        }
        // then make a Result
        Result thisResult = new Result();

        // highlight the title with search terms
        String highlightedText;
        TokenStream tokenStream = an.tokenStream("title", new StringReader(docTitle));

        try {
            highlightedText = hl.getBestFragment(tokenStream, docTitle);

            if ((highlightedText != null) && (highlightedText.length() > 0)) {
                docTitle = highlightedText;
            }
        }
        catch (IOException e1) {
            log.warn("Can't highlight " + docTitle, e1);
        }

        thisResult.setTitle(docTitle);

        // highlight the name with search terms
        /*
         * tokenStream = an.tokenStream("title", new StringReader(docName)); try { highlightedText = hl.getBestFragment(tokenStream,
         * docName); log.debug("name after highlighting: " + highlightedText); if (highlightedText != null &&
         * highlightedText.length() > 0) { docName = highlightedText; } } catch (IOException e1) { log.warn("Can't highlight " +
         * docName, e1); }
         */
        thisResult.setName(docName);
        thisResult.setCollection(docCollection);
        thisResult.setPath(docPath);
        thisResult.setURL(docURL);
        thisResult.setCache(docCache);
        thisResult.setZipName(zipName);
        thisResult.setZipPath(zipPath);
        thisResult.setScore(score);
        thisResult.setISBN(docISBN);

        String text = doc.get("summary");
        if (text == null) {
            text = "";
        }

        // highlight the summary with search terms
        tokenStream = an.tokenStream("summary", new StringReader(text));

        try {
            highlightedText = hl.getBestFragment(tokenStream, text);

            if ((highlightedText != null) && (highlightedText.length() > 0)) {
                text = highlightedText;
            }
        }
        catch (IOException e1) {
            log.warn("Can't highlight " + text, e1);
        }

        thisResult.setSummary(text);

        thisResult.setModificationDate(docDate);
        thisResult.setSize(docSize);
        thisResult.setType(docType);

        return thisResult;
    }

    /**
     * @return Returns the allBoostableFields.
     */
    public String[] getAllBoostableFields() {
        return allBoostableFields;
    }
}
