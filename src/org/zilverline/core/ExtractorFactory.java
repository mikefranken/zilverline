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

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.activation.MimetypesFileTypeMap;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.zilverline.extractors.ExcelExtractor;
import org.zilverline.extractors.FileInfoExtractor;
import org.zilverline.extractors.HTMLExtractor;
import org.zilverline.extractors.PDFExtractor;
import org.zilverline.extractors.PowerPointExtractor;
import org.zilverline.extractors.RTFExtractor;
import org.zilverline.extractors.TextExtractor;
import org.zilverline.extractors.WordExtractor;
import org.zilverline.util.FileUtils;

/**
 * Factory for creating Extractors based on file's extension.
 * 
 * @author Michael Franken
 * @version $Revision: 1.19 $
 * 
 * @see Extractor
 */
public final class ExtractorFactory {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(ExtractorFactory.class);

    /** Map holding mappings from file extension to extractor classname. */
    private Map mappings = new TreeMap();

    /** Map holding mappings from mime types to extractor classname. */
    private Map mimeMappings = new TreeMap();

    /** The Factory ignores case by default. */
    private boolean caseSensitive = false;

    /** The Factory does not store file info for unknown formats by default. */
    private boolean defaultFileinfo = false;

    /**
     * Create a factory with defaults set with the extractors provided by Zilverline.
     * 
     */
    public ExtractorFactory() {
        mappings.put("pdf", "org.zilverline.extractors.PDFExtractor");
        mappings.put("doc", "org.zilverline.extractors.WordExtractor");
        mappings.put("rtf", "org.zilverline.extractors.RTFExtractor");
        mappings.put("html", "org.zilverline.extractors.HTMLExtractor");
        mappings.put("htm", "org.zilverline.extractors.HTMLExtractor");
        mappings.put("txt", "org.zilverline.extractors.TextExtractor");
        mappings.put("xls", "org.zilverline.extractors.ExcelExtractor");
        mappings.put("ppt", "org.zilverline.extractors.PowerPointExtractor");

        addMimeMappings();
    }

    private void addMimeMappings() {
        mimeMappings.put("application/pdf", "org.zilverline.extractors.PDFExtractor");
        mimeMappings.put("*.pdf", "org.zilverline.extractors.PDFExtractor");
        mimeMappings.put("*.pdf/octet-stream", "org.zilverline.extractors.PDFExtractor");
        mimeMappings.put("application/msword", "org.zilverline.extractors.WordExtractor");
        mimeMappings.put("application/rtf", "org.zilverline.extractors.RTFExtractor");
        mimeMappings.put("text/html", "org.zilverline.extractors.HTMLExtractor");
        mimeMappings.put("text/plain", "org.zilverline.extractors.TextExtractor");
        mimeMappings.put("application/vnd.ms-excel", "org.zilverline.extractors.ExcelExtractor");
        mimeMappings.put("application/vnd.ms-powerpoint", "org.zilverline.extractors.PowerPointExtractor");
    }

    /**
     * Returns whether a given File can be extracted based on its extension.
     * 
     * @param f File that needs an Extractor
     * 
     * @return Extractor for File, or null if mapping is unknown
     */
    public boolean canExtract(final File f) {
        log.debug("Can we extract: " + f.getName() + "?");

        String extension = FileUtils.getExtension(f);

        if (!caseSensitive) {
            extension = extension.toLowerCase();
        }

        log.debug("" + mappings.containsKey(extension));

        return mappings.containsKey(extension);
    }

    /**
     * Returns whether a MIME-type can be extracted.
     * 
     * @param type MIME-type that needs an Extractor
     * 
     * @return Extractor for File, or null if mapping is unknown
     */
    public boolean canExtract(final String type) {
        log.debug("Can we extract: " + type + "?");
        String theType = type;
        if (!caseSensitive) {
            theType = type.toLowerCase();
        }
        theType = theType.split(";")[0];
        boolean canExtract = mimeMappings.containsKey(theType);
        log.debug("" + canExtract);
        return canExtract;
    }

    /**
     * Returns an Extractor for a given File, based on its extension. A new Extractor object is created everytime, preventing any
     * threadsafety issues
     * 
     * @param f File that needs an Extractor
     * 
     * @return Extractor for File, or null if mapping is unknown
     */
    public Extractor createExtractor(final File f) {
        log.debug("returning Extractor for: " + f.getName());

        String extension = FileUtils.getExtension(f);

        if (!caseSensitive) {
            extension = extension.toLowerCase();
        }
        

        String className = (String) mappings.get(extension);
        // if we found nothing return the FileInfoExtractor if that's the default
        if (!StringUtils.hasText(className) && defaultFileinfo) className = "org.zilverline.extractors.FileInfoExtractor";

        try {
            if (className != null) {
                Class c = Class.forName(className);

                if (c != null) {
                    log.debug("Returning Extractor: " + className);

                    return (Extractor) c.newInstance();
                }
            }
        }
        catch (InstantiationException e1) {
            log.debug("Can not initiate Extractor '" + className + "' for " + f.getName(), e1);
        }
        catch (IllegalAccessException e1) {
            log.debug("Can not access Extractor " + className + "' for " + f.getName(), e1);
        }
        catch (ClassNotFoundException e) {
            log.debug("Class not found: " + className + "' for " + f.getName(), e);
        }

        log.debug("Unknown format: " + f.getName());

        return null;
    }

    /**
     * Get the MIME-type of a given file.
     * 
     * @param f the File
     * @return the MIME-type of String
     */
    public static String getMimeType(final File f) {
        String type = new MimetypesFileTypeMap().getContentType(f);
        if ("application/octet-stream".equalsIgnoreCase(type)) {
            try {
                Magic parser = new Magic();
                // getMagicMatch accepts Files or byte[],
                // which is nice if you want to test streams
                MagicMatch match = parser.getMagicMatch(f);
                return match.getMimeType();
            }
            catch (MagicParseException e) {
                log.warn("Can't parse " + f.getName(), e);
            }
            catch (MagicMatchNotFoundException e) {
                log.warn("Can't find type for " + f.getName(), e);
            }
            catch (MagicException e) {
                log.warn("Can't find type for " + f.getName(), e);
            }
        }
        return type;
    }

    /**
     * Returns an Extractor for a given MIME-type. A new Extractor object is created everytime, preventing any threadsafety issues
     * 
     * @param type File that needs an Extractor
     * 
     * @return Extractor for MIME-type, or null if mapping is unknown
     */
    public Extractor createExtractor(final String type) {
        log.debug("returning Extractor for: " + type);

        String theType = type;

        if (!caseSensitive) {
            theType = theType.toLowerCase();
        }
        theType = theType.split(";")[0];

        String className = (String) mimeMappings.get(theType);

        try {
            if (className != null) {
                Class c = Class.forName(className);

                if (c != null) {
                    log.debug("Returning Extractor: " + className);

                    return (Extractor) c.newInstance();
                }
            }
        }
        catch (InstantiationException e1) {
            log.debug("Can not initiate Extractor '" + className + "' for " + theType, e1);
        }
        catch (IllegalAccessException e1) {
            log.debug("Can not access Extractor " + className + "' for " + theType, e1);
        }
        catch (ClassNotFoundException e) {
            log.debug("Class not found: " + className + "' for " + theType, e);
        }

        log.warn("Unknown format: " + theType);

        return null;
    }

    /**
     * Set mappings from a Map object. The mappings are file extensions with commands as values. For instance
     * 'pdf=org.zilverline.core.PDFExtractor'.
     * 
     * @param props properties as a Map with extension as key and command as value
     */
    public void setMappings(final Map props) {
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
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the Factory ignores case or not.
     * 
     * @param b indicates whether to handle mappings casesensitively
     */
    public void setCaseSensitive(final boolean b) {
        caseSensitive = b;
    }

    /**
     * Get the mappings for the Factory.
     * 
     * @return the mappings
     */
    public Map getMappings() {
        return mappings;
    }

    /**
     * Find all Extractors on the classpath. This is an expensive operation, use with care.
     * 
     * @return array of names of found Extractors
     */
    public static String[] findExtractorsOnClasspath() {
        log.debug("Known Extractors on classpath");
        String[] extractorNames = null;
        Class[] extractors = { FileInfoExtractor.class, PDFExtractor.class, WordExtractor.class, RTFExtractor.class,
            HTMLExtractor.class, TextExtractor.class, ExcelExtractor.class, PowerPointExtractor.class };
        extractorNames = new String[extractors.length];
        for (int i = 0; i < extractors.length; i++) {
            extractorNames[i] = extractors[i].getName();
            log.debug("Extractor: " + extractors[i].getName());
        }
        return extractorNames;
    }

    public Map getMimeMappings() {
        return mimeMappings;
    }

    public void setMimeMappings(Map mimeMappings) {
        this.mimeMappings = mimeMappings;
    }

	/**
	 * @return the defaultFileinfo
	 */
	public boolean isDefaultFileinfo() {
		return defaultFileinfo;
	}

	/**
	 * @param defaultFileinfo the defaultFileinfo to set
	 */
	public void setDefaultFileinfo(boolean defaultFileinfo) {
		this.defaultFileinfo = defaultFileinfo;
	}

}
