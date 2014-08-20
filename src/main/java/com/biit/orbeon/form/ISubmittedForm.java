package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.CategoryDoesNotExistException;

public interface ISubmittedForm {

	List<ICategory> getCategories();

	void addCategory(ICategory category);

	/**
	 * Gets a category object from its text. If more than one category has the same text, returns the first one.
	 * 
	 * @param categoryText
	 * @return
	 * @throws CategoryDoesNotExistException
	 */
	ICategory getCategory(String categoryText) throws CategoryDoesNotExistException;

	String getFormName();

	String getApplicationName();

	String getId();

}
