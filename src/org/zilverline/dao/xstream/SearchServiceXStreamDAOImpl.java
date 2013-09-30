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

package org.zilverline.dao.xstream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.converters.ConversionException;

import org.zilverline.dao.DAOException;
import org.zilverline.dao.SearchServiceDAO;
import org.zilverline.service.SearchServiceImpl;

/**
 * Implementation of DAO interface using XStream support.
 * 
 * @author Michael Franken
 * @version $Revision: 1.10 $
 */
public class SearchServiceXStreamDAOImpl extends AbstractXStreamDAOImpl implements SearchServiceDAO {
    /**
     * Create a DAO for persisting SearchService.
     */
    public SearchServiceXStreamDAOImpl() {
        xstream.alias("searchService", SearchServiceImpl.class);
        filename = "searchService.xml";
    }

    /**
     * Save the SearchService to the datastore.
     * 
     * @param service the SearchService
     * @throws DAOException if the service can not be stored
     */
    public final void store(final SearchServiceImpl service) throws DAOException {
        try {
            writer = new FileWriter(new File(getBaseDir(), filename));
            xstream.toXML(service, writer);
        }
        catch (IOException e) {
            throw new DAOException("Error saving SearchService", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e1) {
                    throw new DAOException("Error closing file writer", e1);
                }
            }
        }
    }

    /**
     * Retrieve the SearchService from store.
     * 
     * @return SearchService the service
     */
    public final SearchServiceImpl load() {
        SearchServiceImpl service = null;
        try {
            reader = new FileReader(new File(getBaseDir(), filename));
            service = (SearchServiceImpl) xstream.fromXML(reader);
        }
        catch (ConversionException e) {
            log.warn("Inconsistent SearchService data found. Possibly old version. Ignoring old data.", e);
        }
        catch (IOException e) {
            log.warn("No SearchService data found. Possibly first time zilverline runs.", e);
        }
        catch (Exception e) {
            log.warn("Something went wrong, but the show must go on.", e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e1) {
                    log.warn("Can not close file reader", e1);
                }
            }
        }
        return service;
    }
}
