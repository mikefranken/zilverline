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

package org.zilverline.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.html.dom.HTMLDocumentImpl;

/**
 * Extracts contents from an HTML file using the NekoHTML library, based on Lucene in Action Book.
 * 
 * @author Michael Franken
 * @version $Revision: 1.17 $
 */
public final class HTMLExtractor extends AbstractExtractor {
    /**
     * Extract the content from the given HTML file. As a side effect the type, title and summary are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public Reader getContent(final File f) {
        Reader reader = null;

        setType("HTML");
        try {
            DOMFragmentParser parser = new DOMFragmentParser();
            DocumentFragment node = new HTMLDocumentImpl().createDocumentFragment();
            log.debug("start parsing: " + f.getName());
            parser.parse(new InputSource(new FileInputStream(f)), node);
            log.debug("finished parsing: " + f.getName());
            StringBuffer sb = new StringBuffer();
            // get the Title
            getText(sb, node, "title");
            setTitle(sb.toString());
            // get the contents
            sb.setLength(0);
            getText(sb, node);
            reader = new StringReader(sb.toString());
            setSummary(getSummaryFromContent(sb.toString()));
            setISBN(getISBNFromContent(sb.toString()));

            // setSummary(sb.toString().substring(0, Math.min(SUMMARY_SIZE, sb.length())));
        }
        catch (FileNotFoundException e) {
            log.warn("Can't open file: " + f.getName(), e);
        }
        catch (IOException e) {
            log.warn("Can't extract contents for: " + f.getName(), e);
        }
        catch (SAXException e) {
            log.warn("Can't parse contents for: " + f.getName(), e);
        }

        return reader;
    }

    /**
     * Extract the content from the given HTML InputStream.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public String getContent(final InputStream is) {
        try {
            DOMFragmentParser parser = new DOMFragmentParser();
            DocumentFragment node = new HTMLDocumentImpl().createDocumentFragment();
            parser.parse(new InputSource(new InputStreamReader(is)), node);
            StringBuffer sb = new StringBuffer();
            // get the contents
            getText(sb, node);
            return sb.toString();
        }
        catch (IOException e) {
            log.warn("Can't extract contents for: " + is, e);
        }
        catch (SAXException e) {
            log.warn("Can't parse contents for: " + is, e);
        }

        return "";
    }

    /**
     * Extract the content from the given HTML String.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public String getContent(final String s) {
        try {
            DOMFragmentParser parser = new DOMFragmentParser();
            DocumentFragment node = new HTMLDocumentImpl().createDocumentFragment();
            parser.parse(new InputSource(new StringReader(s)), node);
            StringBuffer sb = new StringBuffer();
            // get the contents
            getText(sb, node);
            return sb.toString();
        }
        catch (IOException e) {
            log.warn("Can't extract contents for: " + s, e);
        }
        catch (SAXException e) {
            log.warn("Can't parse contents for: " + s, e);
        }

        return "";
    }

    /**
     * Get all text from the HTML document.
     * 
     * @param sb the buffer to add the contents to.
     * @param node the starting node.
     */
    private void getText(StringBuffer sb, final Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            sb.append(node.getNodeValue());
        }
        NodeList children = node.getChildNodes();
        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++) {
                getText(sb, children.item(i));
            }
        }
    }

    /**
     * Get all text from a specific element in the HTML document.
     * 
     * @param sb the buffer to add the contents to.
     * @param node the starting node.
     * @param element the element, such as 'title'.
     * 
     * @return true if anything was added
     */
    private boolean getText(final StringBuffer sb, final Node node, final String element) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (element.equalsIgnoreCase(node.getNodeName())) {
                getText(sb, node);
                return true;
            }
        }
        NodeList children = node.getChildNodes();
        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++) {
                if (getText(sb, children.item(i), element)) {
                    return true;
                }
            }
        }
        return false;
    }
}
