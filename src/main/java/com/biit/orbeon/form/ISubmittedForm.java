package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.CategoryDoesNotExistException;
import com.biit.orbeon.form.exceptions.QuestionDoesNotExistException;

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

	List<IQuestion> getQuestions();

	String getFormName();

	String getApplicationName();

	String getId();

	/**
	 * Gets a Question object from its tag. If more than one question has the same text, returns the first one.
	 * 
	 * @param questionTag
	 * @return
	 * @throws QuestionDoesNotExistException
	 */
	IQuestion getQuestion(String questionTag) throws QuestionDoesNotExistException;

}
