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

package org.zilverline.util;

/**
 * Contains build information about Zilverline.
 * Class gets generated from template thru build process.
 *
 * @author Michael Franken
 * @version $Revision: 1.7 $
 */
public final class About {
    /** Hidden constructor. */
    private About() {
    }

    private static final String BUILD_DATE = "16-05-2006 21:27:48";
    private static final String BUILD_VERSION = "1.5.0";
    private static final String AUTHOR = "Michael Franken";
    private static final String PRODUCT_NAME = "Zilverline";
    private static final String COPYRIGHT = "Copyright (c) 2004-2006";

    public static String getBuildDate() {
        return BUILD_DATE;
    }

    public static String getBuildVersion() {
        return BUILD_VERSION;
    }

    public static String getAuthor() {
        return AUTHOR;
    }

    public static String getProductName() {
        return PRODUCT_NAME;
    }

    public static String getCopyright() {
        return COPYRIGHT;
    }
}
