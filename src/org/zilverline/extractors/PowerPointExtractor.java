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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;

/**
 * This class extracts text from MS Powerpoint files by using the POI library.
 * 
 * @author Michael Franken
 * @version $Revision: 1.7 $
 */
public class PowerPointExtractor extends AbstractExtractor implements POIFSReaderListener {

    /** Writer to store parsed content. */
    private ByteArrayOutputStream writer = new ByteArrayOutputStream();

    /**
     * Extract the content from the given Powerpoint file. As a side effect the type is set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final Reader getContent(final File f) {

        setType("POWERPOINT");

        try {
            POIFSReader reader = new POIFSReader();
            reader.registerListener(this);
            reader.read(new FileInputStream(f));
            setSummary(getSummaryFromContent(writer.toString()));

            return new InputStreamReader(new ByteArrayInputStream(writer.toByteArray()));
        }
        catch (Exception e) {
            log.warn("Can't extract contents for: " + f.getName(), e);
        }

        return null;
    }

    /**
     * Extract the content from the given Powerpoint file. As a side effect the type is set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        try {
            POIFSReader reader = new POIFSReader();
            reader.registerListener(this);
            reader.read(is);
            return new String(writer.toByteArray());
        }
        catch (Exception e) {
            log.warn("Can't extract contents", e);
        }

        return "";
    }

    /**
     * @see org.apache.poi.poifs.eventfilesystem.POIFSReaderListener#processPOIFSReaderEvent(org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent)
     */
    public void processPOIFSReaderEvent(POIFSReaderEvent event) {
        try {
            if (!event.getName().equalsIgnoreCase("PowerPoint Document")) {
                return;
            }
            DocumentInputStream input = event.getStream();
            byte[] buffer = new byte[input.available()];
            input.read(buffer, 0, input.available());
            for (int i = 0; i < buffer.length - 20; i++) {
                long type = LittleEndian.getUShort(buffer, i + 2);
                long size = LittleEndian.getUInt(buffer, i + 4);
                if (type == 4008) {
                    writer.write(' ');
                    writer.write(buffer, i + 4 + 4, (int) size);
                    i = i + 4 + 4 + (int) size - 1;
                }
            }
        }
        catch (Exception e) {
            log.warn("Error parsing powerpoint", e);
        }
    }
}
