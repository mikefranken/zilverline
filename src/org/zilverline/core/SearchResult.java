/*
 * Copyright 2003-2005 Michael Franken, Zilverline.
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

package org.zilverline.core;

/**
 * The SearchResult represent the result of a Search. It contains the number of hits and the results, as well as the starting and
 * ending number of the result. So possibly an Array of 20 results, from 234 hits, results 21 to 40.
 * 
 * @author Michael Franken
 * @version $Revision: 1.4 $
 * 
 * @see org.zilverline.core.FileSystemCollection
 */
public final class SearchResult {

    /**
     * Create a SearchResult with the number of hits and the results, as well as the starting and ending number of the result. So
     * possibly an Array of 20 results, from 234 hits, results 21 to 40.
     * 
     * @param thisResults the array of Results
     * @param thisNumberOfHits the total number of Hits, probably larger than the results
     * @param thisStartAt the first results 'offset' into all hits
     * @param thisEndAt the last results 'offset' into all hits
     */
    public SearchResult(final Result[] thisResults, final int thisNumberOfHits, final int thisStartAt, final int thisEndAt) {
        results = thisResults;
        numberOfHits = thisNumberOfHits;
        startAt = thisStartAt;
        endAt = thisEndAt;
    }

    /** The results. */
    private Result[] results;

    /** The number of results. */
    private int numberOfHits;

    /** The first results 'offset' into all hits. */
    private int endAt;

    /** The last results 'offset' into all hits. */
    private int startAt;

    /**
     * Returns the the total number of Hits. Probably larger than the size if results
     * 
     * @return numberOfHits
     */
    public int getNumberOfHits() {
        return numberOfHits;
    }

    /**
     * Returns the results as an Array.
     * 
     * @return results
     */
    public Result[] getResults() {
        return results;
    }

    /**
     * Returns offset into all Hits of the last Result.
     * 
     * @return Returns the endAt.
     */
    public int getEndAt() {
        return endAt;
    }

    /**
     * Returns offset into all Hits of the first Result.
     * 
     * @return Returns the startAt.
     */
    public int getStartAt() {
        return startAt;
    }
}
