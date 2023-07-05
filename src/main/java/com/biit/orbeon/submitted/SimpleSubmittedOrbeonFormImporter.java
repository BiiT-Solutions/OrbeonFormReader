package com.biit.orbeon.submitted;

import com.biit.form.submitted.ISubmittedCategory;
import com.biit.form.submitted.ISubmittedForm;
import com.biit.form.submitted.ISubmittedGroup;
import com.biit.form.submitted.ISubmittedObject;
import com.biit.form.submitted.ISubmittedQuestion;
import com.biit.form.submitted.implementation.SubmittedCategory;
import com.biit.form.submitted.implementation.SubmittedForm;
import com.biit.form.submitted.implementation.SubmittedGroup;
import com.biit.form.submitted.implementation.SubmittedObject;
import com.biit.form.submitted.implementation.SubmittedQuestion;
import com.biit.orbeon.OrbeonImporter;

/**
 * Gets the basic information from an orbeon form.
 */
public class SimpleSubmittedOrbeonFormImporter extends OrbeonImporter {

    @Override
    public ISubmittedForm createForm(String applicationName, String formName) {
        return new SubmittedForm(applicationName, formName);
    }

    @Override
    public ISubmittedCategory createCategory(ISubmittedObject parent, String tag) {
        final ISubmittedCategory category = new SubmittedCategory(tag);
        category.setParent((SubmittedObject) parent);
        return category;
    }

    @Override
    public ISubmittedGroup createGroup(ISubmittedObject parent, String tag) {
        final ISubmittedGroup group = new SubmittedGroup(tag);
        group.setParent((SubmittedObject) parent);
        return group;
    }

    @Override
    public ISubmittedQuestion createQuestion(ISubmittedObject parent, String tag) {
        final ISubmittedQuestion question = new SubmittedQuestion(tag);
        question.setParent((SubmittedObject) parent);
        return question;
    }

}
