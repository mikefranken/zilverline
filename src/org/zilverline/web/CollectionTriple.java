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

import org.zilverline.core.DocumentCollection;

/**
 * Triple (id, name, selected) representing a Collection in a SearchForm. Contains name and id of collection, as well as a boolean
 * indicating whether this collection has been selected.
 * 
 * @author Michael Franken
 * @version $Revision: 1.7 $
 */
public final class CollectionTriple {

    /**
     * 
     * Creates a CollectionTriple, consisting of (id, name, selected).
     * 
     * @param thatCollection the Collection
     * @param thatSelected whether this Collection was selected
     */
    public CollectionTriple(final DocumentCollection thatCollection, final boolean thatSelected) {
        this.id = thatCollection.getId();
        this.name = thatCollection.getName();
        this.selected = thatSelected;
    }

    /** The id of the collection. */
    private Long id;

    /** The name of the collection. */
    private String name;

    /** Whether this Collection was selected. */
    private boolean selected;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the selected.
     */
    public boolean isSelected() {
        return selected;
    }

}
