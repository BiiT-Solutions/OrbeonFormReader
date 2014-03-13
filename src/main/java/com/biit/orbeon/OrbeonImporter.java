package com.biit.orbeon;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.biit.orbeon.configuration.OrbeonConfigurationReader;
import com.biit.orbeon.form.IAnswer;
import com.biit.orbeon.form.ICategory;
import com.biit.orbeon.form.IForm;
import com.biit.orbeon.form.IQuestion;

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
	public IForm readFormAnswers(String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
			throws MalformedURLException, DocumentException {
		IForm form = createForm(orbeonApplication, orbeonFormName);
		return readXml(getXml(orbeonApplication, orbeonFormName, orbeonDocumentId), form);
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
	public IForm readFormAnswers(String server, int port, String orbeonApplication, String orbeonFormName,
			String orbeonDocumentId) throws MalformedURLException, DocumentException {
		IForm form = createForm(orbeonApplication, orbeonFormName);
		return readXml(getXml(server, port, orbeonApplication, orbeonFormName, orbeonDocumentId), form);
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
	public String getXml(String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
			throws MalformedURLException, DocumentException {
		return getXml(OrbeonConfigurationReader.getInstance().getOrbeonServer(), OrbeonConfigurationReader
				.getInstance().getOrbeonPort(), orbeonApplication, orbeonFormName, orbeonDocumentId);
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
	public String getXml(String server, int port, String orbeonApplication, String orbeonFormName,
			String orbeonDocumentId) throws MalformedURLException, DocumentException {
		String xmlURL = "http://" + server + ":" + port + "/orbeon/fr/service/persistence/crud/" + orbeonApplication
				+ "/" + orbeonFormName + "/data/" + orbeonDocumentId + "/data.xml";
		SAXReader xmlReader = new SAXReader();

		final Document xmlResponse = xmlReader.read(new URL(xmlURL));
		if (xmlResponse != null) {
			return xmlResponse.asXML();
		}
		return null;
	}

	/**
	 * Transforms the XML of a orbeon forms submitted answers into a Form object.
	 * 
	 * @param xmlText
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("rawtypes")
	public IForm readXml(String xmlText, IForm form) throws DocumentException {
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
					category.addQuestions(getQuestions(category, xmlQuestion, ""));
				}
			}
		}

		return form;
	}

	@SuppressWarnings("rawtypes")
	private List<IQuestion> getQuestions(ICategory category, Element xmlQuestion, String prefix) {
		List<IQuestion> questions = new ArrayList<>();
		// Ignore subcategories
		if (xmlQuestion.getName().startsWith(SUBCATEGORY_PREFIX)) {
			return questions;
		}

		if (prefix.length() > 0) {
			prefix = prefix + ".";
		}
		if (xmlQuestion.getName().startsWith(GROUP_PREFIX)) {
			String groupPrefix = prefix + xmlQuestion.getName().replace(GROUP_PREFIX, "");
			for (Iterator groupIterator = xmlQuestion.elementIterator(); groupIterator.hasNext();) {
				final Element xmlQuestionInGroup = (Element) groupIterator.next();
				// Look up for nested groups.
				questions.addAll(getQuestions(category, xmlQuestionInGroup, groupPrefix));
			}
		} else {
			// It is not in a group.
			IQuestion question = createQuestion(category, prefix + xmlQuestion.getName());
			question.setAnswer(createAnswer(question, xmlQuestion.getText()));
			questions.add(question);
		}
		return questions;
	}

	public abstract IForm createForm(String applicationName, String formName);

	public abstract ICategory createCategory(String tag);

	public abstract IQuestion createQuestion(ICategory category, String tag);

	public abstract IAnswer createAnswer(IQuestion question, String value);

}
