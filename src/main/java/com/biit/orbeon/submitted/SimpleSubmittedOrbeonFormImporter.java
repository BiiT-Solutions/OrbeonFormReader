package com.biit.orbeon.submitted;

import com.biit.form.submitted.ISubmittedCategory;
import com.biit.form.submitted.ISubmittedForm;
import com.biit.form.submitted.ISubmittedGroup;
import com.biit.form.submitted.ISubmittedObject;
import com.biit.form.submitted.ISubmittedQuestion;
import com.biit.form.submitted.implementation.SubmittedCategory;
import com.biit.form.submitted.implementation.SubmittedForm;
import com.biit.form.submitted.implementation.SubmittedGroup;
import com.biit.form.submitted.implementation.SubmittedQuestion;
import com.biit.orbeon.OrbeonImporter;

public class SimpleSubmittedOrbeonFormImporter extends OrbeonImporter {

	@Override
	public ISubmittedForm createForm(String applicationName, String formName) {
		return new SubmittedForm(applicationName, formName);
	}

	@Override
	public ISubmittedCategory createCategory(ISubmittedObject parent, String tag) {
		ISubmittedCategory category = new SubmittedCategory(tag);
		category.setParent(parent);
		return category;
	}

	@Override
	public ISubmittedGroup createGroup(ISubmittedObject parent, String tag) {
		ISubmittedGroup group = new SubmittedGroup(tag);
		group.setParent(parent);
		return group;
	}

	@Override
	public ISubmittedQuestion createQuestion(ISubmittedObject parent, String tag) {
		ISubmittedQuestion question = new SubmittedQuestion(tag);
		question.setParent(parent);
		return question;
	}

}
