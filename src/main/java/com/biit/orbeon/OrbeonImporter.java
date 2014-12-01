package com.biit.orbeon;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
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
import com.biit.orbeon.form.ISubmittedObject;

/**
 * Reads data from Orbeon Form.
 */
public abstract class OrbeonImporter {
	private final static String REPEATABLE_GROUP_SUFIX = "-iterator";

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
	 * @throws UnsupportedEncodingException
	 */
	public ISubmittedForm readFormAnswers(String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
			throws MalformedURLException, DocumentException, UnsupportedEncodingException {
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
	 * @throws UnsupportedEncodingException
	 */
	public ISubmittedForm readFormAnswers(ISubmittedForm form, String orbeonDocumentId) throws MalformedURLException,
			DocumentException, UnsupportedEncodingException {
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
	 * @throws UnsupportedEncodingException
	 */
	public ISubmittedForm readFormAnswers(String protocol, String server, int port, String orbeonApplication,
			String orbeonFormName, String orbeonDocumentId) throws MalformedURLException, DocumentException,
			UnsupportedEncodingException {
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
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public void readXml(String xmlText, ISubmittedForm form) throws DocumentException, UnsupportedEncodingException {
		SAXReader xmlReader = new SAXReader();
		final Document xmlResponse = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
		final Element formElement = xmlResponse.getRootElement();

		for (Iterator formChildren = formElement.elementIterator(); formChildren.hasNext();) {
			final Element xmlCategory = (Element) formChildren.next();
			// Hide email.
			if (!xmlCategory.getName().equals("liferay_email_address")) {
				ICategory category = createCategory(form, xmlCategory.getName());
				form.addChild(category);
				readGroups(xmlCategory, category);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void readGroups(Element xmlParentGroup, ISubmittedObject parent) {
		for (Iterator children = xmlParentGroup.elementIterator(); children.hasNext();) {
			final Element xmlGroupOrQuestion = (Element) children.next();
			// If has nested elements, is a group.
			if (xmlGroupOrQuestion.elementIterator().hasNext()) {
				// If has a "-iterator" is a repeatable group.
				if (xmlGroupOrQuestion.getName().endsWith(REPEATABLE_GROUP_SUFIX)) {
					((IGroup) parent).increaseNumberOfIterations();
					// Ignore dummy iterator group and put questions in the parent
					readGroups(xmlGroupOrQuestion, parent);
				} else {
					IGroup group = createGroup(parent, xmlGroupOrQuestion.getName());
					parent.addChild(group);
					readGroups(xmlGroupOrQuestion, group);
				}
			} else {
				IQuestion question = createQuestion(parent, xmlGroupOrQuestion.getName());
				question.setAnswer(xmlGroupOrQuestion.getText());
				parent.addChild(question);
			}
		}
	}

	public abstract ISubmittedForm createForm(String applicationName, String formName);

	public abstract ICategory createCategory(ISubmittedObject parent, String tag);

	public abstract IGroup createGroup(ISubmittedObject parent, String tag);

	public abstract IQuestion createQuestion(ISubmittedObject parent, String tag);
}
