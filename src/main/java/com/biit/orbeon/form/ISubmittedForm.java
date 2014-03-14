package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.CategoryDoesNotExistException;

public interface ISubmittedForm {

	List<ICategory> getCategories();

	void addCategory(ICategory category);

	ICategory getCategory(String categoryText) throws CategoryDoesNotExistException;

	List<IQuestion> getQuestions();

	String getFormName();

	String getApplicationName();
	
	String getId();

}
