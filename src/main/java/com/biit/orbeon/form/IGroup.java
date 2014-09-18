package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.GroupDoesNotExistException;
import com.biit.orbeon.form.exceptions.QuestionDoesNotExistException;

public interface IGroup extends ICommonAttributes {

	void addGroup(IGroup group);

	void addQuestion(IQuestion questions);

	void addQuestions(List<IQuestion> questions);

	List<IQuestion> getQuestions();

	IGroup getGroup(String tag) throws GroupDoesNotExistException;

	IQuestion getQuestion(String questionTag) throws QuestionDoesNotExistException;
}
