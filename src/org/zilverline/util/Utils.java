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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

/**
 * A Bunch of general purpose utilities .
 * 
 * @author Michael Franken
 * @version $Revision: 1.12 $
 */
public final class Utils {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(Utils.class);

    /**
     * Hidden default constructor.
     */
    private Utils() {
    }

    /**
     * Picks a random number between 0 and (below) the givenNumber.
     * 
     * @param number the givenNumber
     * 
     * @return a number n: 0 &lt;= n &lt; givenNumber
     */
    public static int pickOne(final int number) {
        return (int) Math.round(Math.random() * (double) (number - 1));
    }

    /**
     * Does an 'ELF-proof' on given ISBN number.
     * 
     * @param ISBNnumber the givenNumber
     * 
     * @return true if so
     */
    public static boolean isValidISBNNumber(final String ISBNnumber) {
        // check whether this is a valid ISBN number: see http://www.isbn-international.org/en/userman/chapter4.html
        if (!StringUtils.hasText(ISBNnumber)) {
            log.debug("invalid ISBN: " + ISBNnumber);
            return false;
        }
        // remove all non-valid ISBN characters (0-9xX and - seem valid) and the -
        String number = ISBNnumber.replaceAll("[^0-9xX]", "");
        if (number.length() != 10) {
            log.debug("invalid ISBN: " + number);
            return false;
        }
        char checkDigit = number.charAt(9);
        int checkInt;
        if (checkDigit == 'X') {
            checkInt = 10;
        } else {
            checkInt = Integer.parseInt("" + checkDigit);
        }
        String ISBNnumberPrefix = number.substring(0, 9);

        // elf proof
        int total = checkInt;
        for (int i = 0; i < 9; i++) {
            int k = Integer.parseInt("" + ISBNnumberPrefix.charAt(i));
            total += (10 - i) * k;
        }
        if ((total % 11) != 0) {
            log.debug("invalid ISBN, not ELF proof: " + number);
            return false;
        }
        log.debug("valid ISBN: " + number);

        return true;
    }

}
