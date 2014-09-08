package com.biit.orbeon;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.biit.orbeon.configuration.OrbeonConfigurationReader;
import com.biit.orbeon.form.ICategory;
import com.biit.orbeon.form.IGroup;
import com.biit.orbeon.form.IQuestion;
import com.biit.orbeon.form.ISubmittedForm;

/**
 * Reads data from Orbeon Form.
 */
public abstract class OrbeonImporter {
	private final static String SUBCATEGORY_PREFIX = "subcategory-";
	private final static String GROUP_PREFIX = "group-";

	/**
	 * Read the form answers of a orbeon form.
	 *
	 * @param orbeonApplication
	 *            The application name of the form.
	 * @param orbeonFormName
	 *            The form name.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public ISubmittedForm readFormAnswers(String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
			throws MalformedURLException, DocumentException {
		ISubmittedForm form = createForm(orbeonApplication, orbeonFormName);
		readXml(getXml(orbeonApplication, orbeonFormName, orbeonDocumentId), form);
		return form;
	}

	/**
	 * Read the form answers of a orbeon form.
	 *
	 * @param form
	 *            The form.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public ISubmittedForm readFormAnswers(ISubmittedForm form, String orbeonDocumentId) throws MalformedURLException,
			DocumentException {
		readXml(getXml(form.getApplicationName(), form.getFormName(), orbeonDocumentId), form);
		return form;
	}

	/**
	 * Read the form answers of a orbeon form.
	 *
	 * @param server
	 *            The IP of the server that hosts the orbeon web application.
	 * @param port
	 *            The port of the server that hosts the orbeon web application.
	 * @param orbeonApplication
	 *            The application name of the form.
	 * @param orbeonFormName
	 *            The form name.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public ISubmittedForm readFormAnswers(String protocol, String server, int port, String orbeonApplication,
			String orbeonFormName, String orbeonDocumentId) throws MalformedURLException, DocumentException {
		ISubmittedForm form = createForm(orbeonApplication, orbeonFormName);
		readXml(getXml(protocol, server, port, orbeonApplication, orbeonFormName, orbeonDocumentId), form);
		return form;
	}

	/**
	 * Gets the XML of a orbeon application.
	 *
	 * @param orbeonApplication
	 *            The application name of the form.
	 * @param orbeonFormName
	 *            The form name.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public static String getXml(String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
			throws MalformedURLException, DocumentException {
		return getXml(OrbeonConfigurationReader.getInstance().getOrbeonProtocol(), OrbeonConfigurationReader
				.getInstance().getOrbeonServer(), OrbeonConfigurationReader.getInstance().getOrbeonPort(),
				orbeonApplication, orbeonFormName, orbeonDocumentId);
	}

	/**
	 * Gets the XML of a orbeon application.
	 *
	 * @server orbeon server IP.
	 * @port orbeon server port.
	 * @param orbeonApplication
	 *            The application name of the form.
	 * @param orbeonFormName
	 *            The form name.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public static String getXml(String protocol, String server, int port, String orbeonApplication,
			String orbeonFormName, String orbeonDocumentId) throws MalformedURLException, DocumentException {
		String xmlURL = protocol + "://" + server + ":" + port + "/orbeon/fr/service/persistence/crud/"
				+ orbeonApplication + "/" + orbeonFormName + "/data/" + orbeonDocumentId + "/data.xml";
		SAXReader xmlReader = new SAXReader();

		final Document xmlResponse = xmlReader.read(new URL(xmlURL));
		if (xmlResponse != null) {
			return xmlResponse.asXML();
		}
		return null;
	}

	/**
	 * Adds the user submitted answers into a Form object.
	 *
	 * @param xmlText
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("rawtypes")
	public void readXml(String xmlText, ISubmittedForm form) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		final Document xmlResponse = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes()));
		final Element formElement = xmlResponse.getRootElement();

		for (Iterator formIterator = formElement.elementIterator(); formIterator.hasNext();) {
			final Element xmlCategory = (Element) formIterator.next();
			// Hide email.
			if (!xmlCategory.getName().equals("liferay_email_address")) {
				ICategory category = createCategory(xmlCategory.getName());
				form.addCategory(category);

				for (Iterator sectionIterator = xmlCategory.elementIterator(); sectionIterator.hasNext();) {
					final Element xmlQuestion = (Element) sectionIterator.next();
					// Filter subcategories
					// category.addQuestions(getQuestions(category, xmlQuestion,
					// ""));
					createGroupsAnsQuestions(category, null, xmlQuestion);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void createGroupsAnsQuestions(ICategory category, IGroup group, Element xmlQuestion) {
		// List<IQuestion> questions = new ArrayList<>();
		// Ignore subcategories
		if (xmlQuestion.getName().startsWith(SUBCATEGORY_PREFIX)) {
			return;
		}
//		if (prefix.length() > 0) {
//			prefix = prefix + ".";
//			System.out.println("GROUP PREFIX: " + prefix);
//		}

		// Analyzing group
		if (xmlQuestion.getName().startsWith(GROUP_PREFIX)) {
			// Remove 'group-' prefix and '-1' sufix of groups.
			String groupPrefix = xmlQuestion.getName().replace(GROUP_PREFIX, "");
			// Subgroups
			if (group != null) {
//				System.out.println("GROUP: " + groupPrefix + " in GROUP: " + group.getTag() + " CREATED.");
				// Create a group children of a previous group
				IGroup subgroup = createGroup(group, groupPrefix);
				group.addGroup(subgroup);
				// The new active group is the group created
				group = subgroup;
			} else {
//				System.out.println("GROUP: " + groupPrefix + " in CATEGORY: " + category.getTag() + " CREATED.");
				// Create group under category
				group = createGroup(category, groupPrefix);
				category.addGroup(group);
			}
			for (Iterator groupIterator = xmlQuestion.elementIterator(); groupIterator.hasNext();) {
				final Element xmlQuestionInGroup = (Element) groupIterator.next();
				// Look up for nested groups.
				createGroupsAnsQuestions(category, group, xmlQuestionInGroup);
			}
		} else {
			IQuestion question = null;
			if (group == null) {
//				System.out.println("QUESTION: " + prefix + xmlQuestion.getName() + " in CATEGORY: " + category.getTag()
//						+ " CREATED.");
				// It is not in a group.
				question = createQuestion(category, xmlQuestion.getName());
				// The value is always going to be a String class
				question.setAnswer(xmlQuestion.getText());
				category.addQuestion(question);
			}
			// Question belongs to a group
			else {
//				System.out.println("QUESTION: " + prefix + xmlQuestion.getName() + " in GROUP: " + group.getTag()
//						+ " CREATED.");
				question = createQuestion(group, xmlQuestion.getName());
				// The value is always going to be a String class
				question.setAnswer(xmlQuestion.getText());
				group.addQuestion(question);
			}
		}
	}

//	@SuppressWarnings("rawtypes")
//	private List<IQuestion> getQuestions(ICategory category, Element xmlQuestion, String prefix) {
//		List<IQuestion> questions = new ArrayList<>();
//		// Ignore subcategories
//		if (xmlQuestion.getName().startsWith(SUBCATEGORY_PREFIX)) {
//			return questions;
//		}
//
//		if (prefix.length() > 0) {
//			prefix = prefix + ".";
//		}
//		if (xmlQuestion.getName().startsWith(GROUP_PREFIX)) {
//			// Remove 'group-' prefix and '-1' sufix of groups.
//			String groupPrefix = prefix + xmlQuestion.getName().replace(GROUP_PREFIX, "");
//			int sufixStartsAt = groupPrefix.lastIndexOf('-');
//			if (sufixStartsAt > 0) {
//				groupPrefix = groupPrefix.substring(0, sufixStartsAt);
//			}
//			// Create the groups
//
//			for (Iterator groupIterator = xmlQuestion.elementIterator(); groupIterator.hasNext();) {
//				final Element xmlQuestionInGroup = (Element) groupIterator.next();
//				// Look up for nested groups.
//				questions.addAll(getQuestions(category, xmlQuestionInGroup, groupPrefix));
//			}
//		} else {
//			// It is not in a group.
//			IQuestion question = createQuestion(category, prefix + xmlQuestion.getName());
//			// The value is always going to be a String class
//			question.setAnswer(xmlQuestion.getText());
//			questions.add(question);
//		}
//		return questions;
//	}

	public abstract ISubmittedForm createForm(String applicationName, String formName);

	public abstract ICategory createCategory(String tag);

	public abstract IGroup createGroup(ICategory category, String tag);

	public abstract IGroup createGroup(IGroup group, String tag);

	public abstract IQuestion createQuestion(IGroup group, String tag);

	public abstract IQuestion createQuestion(ICategory category, String tag);
}
