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
package org.zilverline.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.zilverline.core.DocumentCollection;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.IMAPCollection;
import org.zilverline.core.IndexException;

/**
 * JavaBean form controller that is used to edit or delete a
 * <code>Collection</code>.
 * 
 * @author Michael Franken
 */
public class EditCollectionController extends AbstractZilverController {

	/**
	 * Adjust the CommandClass based on the implementation used for
	 * DocumentCollection.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {
		// get the Collection (from the collectionManager) referred to by id in
		// the request
		DocumentCollection collection = collectionManager
				.getCollection(new Long(RequestUtils.getRequiredLongParameter(
						request, "collectionId")));
		if (collection instanceof IMAPCollection) {
			setFormView("collectionIMAPForm");
			setCommandClass(IMAPCollection.class);
			setValidator(null);
			return (IMAPCollection) collection;
		} else if (collection instanceof FileSystemCollection) {
			setFormView("collectionForm");
			setCommandClass(FileSystemCollection.class);
			setValidator(new CollectionValidator());
			return (FileSystemCollection) collection;
		}

		return (FileSystemCollection) collection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#suppressValidation(javax.servlet.http.HttpServletRequest)
	 */
	protected boolean suppressValidation(HttpServletRequest request) {
		if (request.getParameter("delete") != null) {
			log.debug(" Suppressing validation for Delete");
			return true;
		}
		return super.suppressValidation(request);
	}

	/** Method updates an existing DocumentCollection. */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws ServletException {
		DocumentCollection collection = (DocumentCollection) command;
		try {
			if (request.getParameter("delete") != null) {
				// delegate the delete to the service layer
				collectionManager.deleteCollection(collection);
			} else {
				// delegate the update to the service layer
				collection.init();
			}
			collectionManager.store();
		} catch (IndexException e) {
			throw new ServletException(
					"Error initializing updated index in EditCollectionForm", e);
		}
		return new ModelAndView(getSuccessView());
	}
}
