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

package org.zilverline.core;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * ArchiveHandler handles the mapping from an archive's extension to its unArchiving program. For now maps extensions to external
 * programs. An empty value denotes a zip file, that can be unzipped using java.util.zip
 * 
 * <p>
 * These are mappings used by zilverline to handle archives based on file extensions. The application is called from within the
 * appropriate environment and working directory by zilverline, zilverline expects the external program to run on the archive by
 * appending its name.
 * </p>
 * 
 * <p>
 * So if for example you specify the mapping <code>"cab => d:\PACL\PAEXT.EXE -d -q -o+"</code> make sure
 * <code>d:\PACL\PAEXT.EXE -d
 * -q -o+ archive.cab</code> is a valid command on your system. Either specify full path, or the
 * program should be on your path. Don't use spaces in the name of the program. By default the extensions are treated case
 * insensitively, but you can change that. If you don't specify a program the internal unzip (java.util.zip) is used.
 * </p>
 * 
 * @author Michael Franken
 * @version $Revision: 1.9 $
 */
public class Handler {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(Handler.class);

    /** The Handler ignores case by default. */
    private boolean caseSensitive = false;
    
    /**
     * Returns whether files with the given extension can be unPacked.
     * 
     * @param type String the extension without the '.'. Can be lower or uppercase.
     * 
     * @return true if we can
     */
    public boolean canUnPack(final String extension) {
        log.debug("Can we unPack: " + extension + "?");
        String theType = extension;
        if (!caseSensitive) {
            theType = extension.toLowerCase();
        }
        //theType = theType.split(";")[0];
        boolean canUnArchive = mappings.containsKey(theType);
        log.debug("" + canUnArchive);
        return canUnArchive;
    }

    /**
     * Returns the UnArciving Command with which files with the given extension can be unPacked.
     * 
     * @param type String the extension without the '.'. Can be lower or uppercase.
     * 
     * @return the command if any, otherwise null
     */
    public String getUnArchiveCommand(final String extension){
        log.debug("Can we unPack: " + extension + "?");
        String theType = extension;
        if (!caseSensitive) {
            theType = extension.toLowerCase();
        }
        return (String) mappings.get(theType);
    }

    /**
     * Mapping from archive's file extension to the unArchiving program including options.
     */
    private Map mappings = new TreeMap();

    /**
     * Initialize ArchiveHandler with default mapping.
     */
    public Handler() {
        mappings.put("zip", "");
    }

    /**
     * Finds all Analyzers on the classpath. This is an expensive operation, use with care.
     * 
     * @return array of names of found Analyzers
     */
    public static String[] findAnalyzersOnClasspath() {
        log.debug("Known Analyzers on classpath");
        String[] allAnalyzers = null;
        Class[] analyzers = { BrazilianAnalyzer.class, ChineseAnalyzer.class, CJKAnalyzer.class, CzechAnalyzer.class,
            DutchAnalyzer.class, FrenchAnalyzer.class, GermanAnalyzer.class, GreekAnalyzer.class, KeywordAnalyzer.class,
            PerFieldAnalyzerWrapper.class, RussianAnalyzer.class, SimpleAnalyzer.class, StandardAnalyzer.class, StopAnalyzer.class,
            WhitespaceAnalyzer.class };
        allAnalyzers = new String[analyzers.length];
        for (int i = 0; i < analyzers.length; i++) {
            // Class clazz = analyzers[i];
            allAnalyzers[i] = analyzers[i].getName();
            log.debug("Analyzer: " + analyzers[i].getName());
        }
        return allAnalyzers;

    }

    /**
     * Set mappings from a Properties object. The properties are file extensions with commands as values. For instance 'chm=hh.exe
     * -decompile .'.
     * 
     * @param props map with extension as key and command (including options) as value.
     */
    public final void setMappings(final Map props) {
        mappings.clear();
        if (caseSensitive) {
            // copy as-is
            mappings.putAll(props);
        } else {
            // convert the keys to lowercase
            Iterator iter = props.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry element = (Map.Entry) iter.next();
                mappings.put(((String) element.getKey()).toLowerCase(), element.getValue());
            }
        }
        log.debug("Map now is: " + mappings);
    }

    /**
     * Check whether the Factory ignores case or not.
     * 
     * @return value indicating case sensitivity
     */
    public final boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the Factory ignores case or not.
     * 
     * @param b indicates whether to handle mappings casesensitively
     */
    public final void setCaseSensitive(final boolean b) {
        caseSensitive = b;
    }

    /**
     * Gets the mappings for the archives as a Map.
     * 
     * @return handlerMap
     */
    public final Map getMappings() {
        return mappings;
    }
}
