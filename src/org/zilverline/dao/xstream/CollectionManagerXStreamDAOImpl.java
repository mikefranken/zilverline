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

import org.zilverline.core.ExtractorFactory;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.Handler;
import org.zilverline.dao.CollectionManagerDAO;
import org.zilverline.dao.DAOException;
import org.zilverline.service.CollectionManager;
import org.zilverline.service.CollectionManagerImpl;

/**
 * Implementation of CollectionManagerDAO interface using XStream.
 * 
 * @author Michael Franken
 * @version $Revision: 1.11 $
 */
public class CollectionManagerXStreamDAOImpl extends AbstractXStreamDAOImpl implements CollectionManagerDAO {

    /**
     * Create a DAO for persisting CollectionManager.
     */
    public CollectionManagerXStreamDAOImpl() {
    	super();
        xstream.alias("collectionManager", CollectionManagerImpl.class);
        xstream.alias("collection", FileSystemCollection.class);
        filename = "collectionManager.xml";
    }

    /**
     * Save the CollectionManager to the datastore.
     * 
     * @param manager the CollectionManager
     * @throws DAOException if the manager can not be stored
     */
    public final void store(final CollectionManager manager) throws DAOException {
        try {
            writer = new FileWriter(new File(getBaseDir(), filename));
            xstream.toXML(manager, writer);
        }
        catch (Exception e) {
            throw new DAOException("Error saving collections", e);
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
     * Retrieve the CollectionManager from store.
     * 
     * @return CollectionManager the Manager
     */
    public final CollectionManager load() {
        CollectionManager manager = null;
        try {
            reader = new FileReader(new File(getBaseDir(), filename));
            manager = (CollectionManager) xstream.fromXML(reader);
            log
                .debug("Reading " + manager.getCollections().size() + " collections from store: "
                    + new File(getBaseDir(), filename));
        }
        catch (ConversionException e) {
            log.warn("Inconsistent CollectionManager data found. Possibly old version. Ignoring old data.", e);
        }
        catch (IOException e) {
            log.warn("Can not read collections. Possibly first time zilverline runs.", e);
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
            manager = checkManagerIntegrity(manager);
        }
        return manager;
    }

    private CollectionManager checkManagerIntegrity(CollectionManager manager) {
        if (manager == null) {
            log.warn("Invalid collectionManager configuration found, resetting to defaults");
            manager = new CollectionManagerImpl();
        }
        if (manager.getFactory() == null || manager.getArchiveHandler() == null || manager.getFactory().getMappings() == null
            || manager.getFactory().getMimeMappings() == null || manager.getArchiveHandler().getMappings() == null) {
            log.warn("Invalid collectionManager configuration found, resetting to defaults");
            manager.setFactory(new ExtractorFactory());
            manager.setArchiveHandler(new Handler());
        }
        return manager;
    }
}
