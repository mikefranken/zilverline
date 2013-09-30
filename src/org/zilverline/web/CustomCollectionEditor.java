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

package org.zilverline.web;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.zilverline.core.DocumentCollection;
import org.zilverline.service.CollectionManager;

/***********************************************************************************************************************************
 * CustomCollectionEditor maps an array of Collection names to a (possibly longer) array of CollectionTriple. If the name is in the
 * array, the collection was selected in the form, and will be set to selected in the CollectionTriple. All non-selected Collections
 * will be retrieved from the CollectionManager, and selected will be set to false.
 * 
 * @see CollectionTriple
 * 
 * @author Michael Franken
 */
public class CustomCollectionEditor extends PropertyEditorSupport {

    /** * logger for Commons logging. */
    private static Log log = LogFactory.getLog(CustomCollectionEditor.class);

    private CollectionManager manager;

    public CustomCollectionEditor(CollectionManager manager) {
        super();
        this.manager = manager;
    }

    public String getAsText() {
        log.debug("In CustomCollectionEditor.getAsText: " + super.getAsText());
        return super.getAsText();
    }

    public Object getValue() {
        log.debug("In CustomCollectionEditor.getValue: " + super.getValue());
        return super.getValue();
    }

    // assume we get a comma separated list of collection names from request
    public void setAsText(String text) throws IllegalArgumentException {
        log.debug("In CustomCollectionEditor.setText for " + text);
        // create a String array to feed to setValue
        setValue(StringUtils.commaDelimitedListToStringArray(text));
    }

    public void setValue(Object text) throws IllegalArgumentException {
        // get the type right, since this can be called from different places, we just want String[]
        log.debug("In CustomCollectionEditor.setValue for " + text);
        String[] texts = null;
        if (text instanceof String[]) {
            texts = (String[]) text;
        } else if (text == null) {
            log.debug("In CustomCollectionEditor.setValue for null");
            return;
        } else {
            log.debug("In CustomCollectionEditor.setValue for unknown class" + text.getClass().getName());
            return;
        }

        log.debug("In CustomCollectionEditor.setValue for " + Arrays.asList(texts));

        try {
            List searchCollections = new ArrayList();
            Iterator it = manager.getCollections().iterator();
            while (it.hasNext()) {
                DocumentCollection thisCollection = (DocumentCollection) it.next();
                String name2Match = thisCollection.getName();
                if (text != null) {
                    boolean match = false;
                    for (int i = 0; i < texts.length; i++) {
                        String col = texts[i].trim();
                        // check whether this collection was selected
                        if (name2Match.equals(col)) {
                            log.debug("adding: " + name2Match + ", true");
                            searchCollections.add(new CollectionTriple(thisCollection, true));
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        log.debug("adding: " + name2Match + ", false");
                        searchCollections.add(new CollectionTriple(thisCollection, false));
                    }
                } else {
                    // if nothing specified, assume you want all, this is handy for google type of external queries
                    searchCollections.add(new CollectionTriple(thisCollection, true));
                }
                super.setValue(searchCollections.toArray());
            }
        }
        catch (Throwable e) {
            throw new IllegalArgumentException("Error converting " + text + " to CollectionTriple[]");
        }
    }
}