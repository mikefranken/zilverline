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

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This class extracts text from MS Excel files by using the POI library.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public class ExcelExtractor extends AbstractExtractor {
    /**
     * Extract the content from the given Excel file. As a side effect the type is set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final Reader getContent(final File f) {
        Reader reader = null;

        setType("EXCEL");

        try {
            CharArrayWriter writer = new CharArrayWriter();

            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(f));
            HSSFWorkbook workbook = new HSSFWorkbook(fs);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);

                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    HSSFRow row = (HSSFRow) rows.next();

                    Iterator cells = row.cellIterator();
                    while (cells.hasNext()) {
                        HSSFCell cell = (HSSFCell) cells.next();
                        switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            String num = Double.toString(cell.getNumericCellValue()).trim();
                            if (num.length() > 0) {
                                writer.write(num + " ");
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            String text = cell.getStringCellValue().trim();
                            if (text.length() > 0) {
                                writer.write(text + " ");
                            }
                            break;
                        default: // skip
                        }
                    }
                }
            }
            setSummary(getSummaryFromContent(writer.toString()));

            return new CharArrayReader(writer.toCharArray());
        }
        catch (Exception e) {
            log.warn("Can't extract contents for: " + f.getName(), e);
        }

        return reader;
    }

    /**
     * Extract the content from the given Excel file. As a side effect the type is set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        try {
            CharArrayWriter writer = new CharArrayWriter();

            POIFSFileSystem fs = new POIFSFileSystem(is);
            HSSFWorkbook workbook = new HSSFWorkbook(fs);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);

                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    HSSFRow row = (HSSFRow) rows.next();

                    Iterator cells = row.cellIterator();
                    while (cells.hasNext()) {
                        HSSFCell cell = (HSSFCell) cells.next();
                        switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            String num = Double.toString(cell.getNumericCellValue()).trim();
                            if (num.length() > 0) {
                                writer.write(num + " ");
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            String text = cell.getStringCellValue().trim();
                            if (text.length() > 0) {
                                writer.write(text + " ");
                            }
                            break;
                        default: // skip
                        }
                    }
                }
            }

            return new String(writer.toCharArray());
        }
        catch (Exception e) {
            log.warn("Can't extract contents", e);
        }

        return "";
    }
}
