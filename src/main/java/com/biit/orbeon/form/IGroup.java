package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.QuestionDoesNotExistException;

public interface IGroup extends ICommonAttributes{

	void addGroup(IGroup group);

	void addQuestion(IQuestion questions);

	void addQuestions(List<IQuestion> questions);

	List<IQuestion> getQuestions();

	IQuestion getQuestion(String questionTag) throws QuestionDoesNotExistException;
}
