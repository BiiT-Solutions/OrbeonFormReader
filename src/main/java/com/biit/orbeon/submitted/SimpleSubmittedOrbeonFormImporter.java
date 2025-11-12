package com.biit.orbeon.submitted;

/*-
 * #%L
 * Orbeon Form Reader
 * %%
 * Copyright (C) 2014 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
