package com.biit.orbeon.form;

import java.util.List;

import com.biit.orbeon.form.exceptions.GroupDoesNotExistException;
import com.biit.orbeon.form.exceptions.QuestionDoesNotExistException;

public interface ICategory extends IGroup {

	@Override
	void addQuestion(IQuestion questions);

	@Override
	void addQuestions(List<IQuestion> questions);

	@Override
	List<IQuestion> getQuestions();

	@Override
	IQuestion getQuestion(String questionTag) throws QuestionDoesNotExistException;

	@Override
	void addGroup(IGroup group);

	void addGroups(List<IGroup> groups);

	List<IGroup> getGroups();

	IGroup getGroup(String tag) throws GroupDoesNotExistException;

}
