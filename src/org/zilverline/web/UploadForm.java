package org.zilverline.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IndexException;

/**
 * Controller deals with uploading files, adding them to the corresponding collection, and indexing the file.
 * 
 * @author Michael Franken
 */
public class UploadForm extends AbstractZilverController {

    private static final Log log = LogFactory.getLog(UploadForm.class);

    public UploadForm() {
        setSessionForm(true);
        setBindOnNewForm(true);
    }

    /**
     * Callback function for reference data: the collections.
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#referenceData(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object, org.springframework.validation.Errors)
     */
    protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
        List theCollections = collectionManager.getCollections();
        List collectionNames = new ArrayList();
        Map map = new HashMap();
        for (Iterator iter = theCollections.iterator(); iter.hasNext();) {
            try {
                FileSystemCollection thisCol = ((FileSystemCollection) iter.next());
                if (thisCol.existsOnDisk()) {
                    collectionNames.add(thisCol.getName());
                } else {
                    log.debug("not a collections");
                }
            }
            catch (ClassCastException e) {
                // do nothing this is not a FileSytemCollection
            }
        }
        map.put("collections", collectionNames);
        return map;
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
        throws Exception {
        UploadCommand upload = (UploadCommand) command;

        Map model = new HashMap();
        try {
            model.put("collection", upload.getCollectionName());
            FileSystemCollection thisCollection = (FileSystemCollection) collectionManager
                .getCollectionByName(upload.getCollectionName());
            if (thisCollection != null) {
                List names = new ArrayList();
                for (Iterator myIter = upload.getFiles().values().iterator(); myIter.hasNext();) {
                    MultipartFile file = (MultipartFile) myIter.next();
                    if (file != null && file.getSize() > 0) {
                        String name = getFileName(file.getOriginalFilename());
                        log.debug("Handling upload " + name);

                        File uploadFileInDestination = new File(thisCollection.getContentDir(), name);
                        log.info("Uploading file " + uploadFileInDestination);
                        file.transferTo(uploadFileInDestination);
                        thisCollection.indexFile(uploadFileInDestination);
                        names.add(name);
                    }
                }
                model.put("files", names);
            } else {
                log.error("Error finding collection:" + upload.getCollectionName());
            }
            upload.setFileCount(1);
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // } catch (MaxUploadSizeExceededException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return showForm(request, errors, getSuccessView(), model);
    }

    private String getFileName(String originalFilename) {
        int index = originalFilename.lastIndexOf("/");
        if (index == -1)
            index = originalFilename.lastIndexOf("\\");
        if (index == -1)
            index = originalFilename.lastIndexOf(":");
        String name = index == -1 ? originalFilename : originalFilename.substring(index + 1);
        return name;
    }

    /**
     * Get the uploaded file(s) from request into proper map. (Convert key from files[file0] to file0)
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBind(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object)
     */
    protected void onBind(HttpServletRequest request, Object command) {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest myRequest = (MultipartHttpServletRequest) request;

            UploadCommand myCommand = (UploadCommand) command;
            Map myFileList = new HashMap();
            for (Iterator myIter = myRequest.getFileMap().keySet().iterator(); myIter.hasNext();) {
                String mySourceKey = (String) myIter.next();
                MultipartFile myFile = (MultipartFile) myRequest.getFileMap().get(mySourceKey);
                if (myFile.getSize() > 0) {
                    String myTargetKey = mySourceKey.substring(mySourceKey.indexOf('[') + 1, mySourceKey.indexOf(']'));
                    myFileList.put(myTargetKey, myFile);
                }

            }
            myCommand.setFiles(myFileList);
        }
    }
}
