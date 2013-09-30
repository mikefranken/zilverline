package org.zilverline.web;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import org.zilverline.core.FileSystemCollection;
import org.zilverline.service.CollectionManager;

/**
 * @author Alon Salant
 * @author Kees van Dieren Modified somewhat for better validation handling
 */
public class UploadValidator implements Validator {

    private static final Log log = LogFactory.getLog(UploadValidator.class);

    protected CollectionManager collectionManager;

    /**
     * Set the collectionManager.
     * 
     * @param cm the CollectionManager
     */
    public void setCollectionManager(final CollectionManager cm) {
        collectionManager = cm;
    }

    public boolean supports(Class clazz) {
        return UploadCommand.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        UploadCommand upload = (UploadCommand) obj;
        FileSystemCollection thisCollection = (FileSystemCollection) collectionManager.getCollectionByName(upload.getCollectionName());
        if (thisCollection == null) {
            errors.rejectValue("collectionName", "error.upload.collection", new Object[] { upload.getCollectionName() }, upload
                .getCollectionName()
                + " must be an existing filesystem collection");
        }

        boolean hasUpload = false;
        Object myKey;
        MultipartFile file;
        for (Iterator myIter = upload.getFiles().keySet().iterator(); myIter.hasNext();) {
            myKey = myIter.next();
            file = (MultipartFile) upload.getFiles().get(myKey);
            log.debug("Validating file " + file.getOriginalFilename());
            if ((file != null) && (file.getSize() > 0)) {
                hasUpload = true;
                // TODO: determine whether to check for allowed content type. Now it is just a hassle since we can't determine given handlers
//                if (!collectionManager.getFactory().canExtract(file.getContentType())) {
//                    errors.rejectValue("files[" + myKey + ']', "error.upload.contentType", new Object[] { file.getContentType() },
//                        file.getContentType() + " (for " + file.getOriginalFilename() + ") is not a supported content-Type");
//                }
            }
        }

        if (!hasUpload) {
            errors.reject("error.upload.required", "At least one file is required");
        }
    }

}
