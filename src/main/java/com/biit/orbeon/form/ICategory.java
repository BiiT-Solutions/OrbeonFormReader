package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.QuestionDoesNotExistException;

public interface ICategory {

	void setTag(String tag);

	String getTag();

	void setText(String text);

	String getText();

	void addQuestions(List<IQuestion> questions);

	List<IQuestion> getQuestions();

	IQuestion getQuestion(String questionTag) throws QuestionDoesNotExistException;

}
