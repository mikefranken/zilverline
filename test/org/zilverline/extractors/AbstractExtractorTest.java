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

package org.zilverline.extractors;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractExtractorTest extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(AbstractExtractorTest.class);

    public void testGetSummaryFromContent() {
        assertEquals("", AbstractExtractor.getSummaryFromContent(null));
        assertEquals("", AbstractExtractor.getSummaryFromContent(""));
        String content = "This is content, and nothing can change that. Unless of course you edit this text, or replace it with something else.";
        String moreContent = "Finished at: Wed May 18 11:41:24 CEST 2005.";
        String troubleContent = "\n \t\t\t     Appendix A\n\tAppendix B.";
        assertEquals(content, AbstractExtractor.getSummaryFromContent(content));
        assertEquals(moreContent, AbstractExtractor.getSummaryFromContent(moreContent));
        assertEquals(" Appendix A Appendix B.", AbstractExtractor.getSummaryFromContent(troubleContent));
        log.debug(AbstractExtractor.getSummaryFromContent(troubleContent));
    }

    public void testGetISBNFromContent() {
        assertEquals("", AbstractExtractor.getISBNFromContent(null));
        assertEquals("", AbstractExtractor.getISBNFromContent(""));
        String content = "This is content, ISBN:0764543857. Unless of course you edit this text, or replace it with something else.";
        String moreContent = "Finished at: ISBN 0-20130-998-X May 18 11:41:24 CEST 2005";
        String troubleContent = "\n \t\t\t  isbn 1-930110-30-831/2\n$&Printed in the.";
        assertEquals("0764543857", AbstractExtractor.getISBNFromContent(content));
        assertEquals("020130998X", AbstractExtractor.getISBNFromContent(moreContent));
        assertEquals("1930110308", AbstractExtractor.getISBNFromContent(troubleContent));
        assertEquals("", AbstractExtractor.getISBNFromContent("ISBN 0-20630-998-X, Unless of course you edit this text, "));
        assertEquals("", AbstractExtractor.getISBNFromContent("jhkghjk"));
        log.debug(AbstractExtractor.getISBNFromContent(troubleContent));
    }

}
