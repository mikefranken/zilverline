package org.zilverline.web;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaBean for the upload command.
 * 
 * @author Michael Franken
 */
public class UploadCommand {

    private String collectionName;

    private int theFileCount = 1;

    private Map theFiles = new HashMap();

    public void setFiles(Map aFileList) {
        theFiles = new HashMap(aFileList);
    }

    public Map getFiles() {
        return theFiles;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String newCollectionName) {
        this.collectionName = newCollectionName;
    }

    public int getFileCount() {
        return theFileCount;
    }

    public void setFileCount(int aFileCount) {
        theFileCount = aFileCount;
    }
}
