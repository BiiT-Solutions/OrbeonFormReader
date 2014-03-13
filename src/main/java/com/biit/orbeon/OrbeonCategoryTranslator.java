package com.biit.orbeon;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.biit.orbeon.configuration.OrbeonConfigurationReader;
import com.biit.orbeon.exceptions.CategoryNameWithoutTranslation;
import com.biit.orbeon.form.ICategory;
import com.biit.orbeon.form.IForm;

public class OrbeonCategoryTranslator {
	private final static String CATEGORY_PREFIX = "category-";
	private HashMap<String, HashMap<String, String>> formTagsToName;
	private static OrbeonCategoryTranslator instance = new OrbeonCategoryTranslator();

	private OrbeonCategoryTranslator() {
		formTagsToName = new HashMap<String, HashMap<String, String>>();
	}

	public static OrbeonCategoryTranslator getInstance() {
		return instance;
	}

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
	 * @throws CategoryNameWithoutTranslation
	 */
	public HashMap<String, String> readFormCategoryTranslations(String orbeonApplication, String orbeonFormName,
			IForm form) throws MalformedURLException, DocumentException, CategoryNameWithoutTranslation {
		return readXml(form, getXml(orbeonApplication, orbeonFormName));
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
	 * @throws CategoryNameWithoutTranslation
	 */
	public HashMap<String, String> readFormCategoryTranslations(String server, int port, String orbeonApplication,
			String orbeonFormName, IForm form) throws MalformedURLException, DocumentException,
			CategoryNameWithoutTranslation {
		return readXml(form, getXml(server, port, orbeonApplication, orbeonFormName));
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
	public String getXml(String orbeonApplication, String orbeonFormName) throws MalformedURLException,
			DocumentException {
		return getXml(OrbeonConfigurationReader.getInstance().getOrbeonServer(), OrbeonConfigurationReader
				.getInstance().getOrbeonPort(), orbeonApplication, orbeonFormName);
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
	 * @return a form with all user answers.
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public String getXml(String server, int port, String orbeonApplication, String orbeonFormName)
			throws MalformedURLException, DocumentException {
		String xmlURL = "http://" + server + ":" + port + "/orbeon/fr/service/persistence/crud/" + orbeonApplication
				+ "/" + orbeonFormName + "/form/form.xhtml";
		SAXReader xmlReader = new SAXReader();

		final Document xmlResponse = xmlReader.read(new URL(xmlURL));
		if (xmlResponse != null) {
			return xmlResponse.asXML();
		}
		return null;
	}

	/**
	 * Create a relationship between category tags and its names.
	 * 
	 * @param xmlText
	 * @return
	 * @throws DocumentException
	 * @throws CategoryNameWithoutTranslation
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> readXml(IForm form, String xmlText) throws DocumentException,
			CategoryNameWithoutTranslation {
		if (formTagsToName.get(form.getId()) != null) {
			return formTagsToName.get(form.getId());
		}

		HashMap<String, String> tagsToName = new HashMap<String, String>();
		SAXReader xmlReader = new SAXReader();
		final Document xmlResponse = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes()));
		final Node formElement = xmlResponse.getRootElement();
		// Categories resource has de id "fr-form-resources".
		Node resourcesSection = formElement.selectSingleNode("//xf:instance[@id='fr-form-resources']");
		// Get the language.
		Node language = resourcesSection.selectSingleNode("//resources/resource[@xml:lang='en']");
		// Ignore nodes that are not categories.
		List<Node> nodes = language.selectNodes("child::node()");
		for (Node node : nodes) {
			if (node != null && node.getName() != null && node.getName().startsWith(CATEGORY_PREFIX)) {
				Node label = node.selectSingleNode("./label");
				tagsToName.put(node.getName(), label.getStringValue());
			}
		}

		formTagsToName.put(form.getId(), tagsToName);
		updateForm(form);

		return tagsToName;
	}

	/**
	 * Create a relationship between category tags and its names.
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
	 * @return
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws CategoryNameWithoutTranslation
	 */
	public HashMap<String, String> readXml(String server, int port, String orbeonApplication, String orbeonFormName,
			IForm form) throws DocumentException, MalformedURLException, CategoryNameWithoutTranslation {
		return readXml(form, getXml(server, port, orbeonApplication, orbeonFormName));
	}

	/**
	 * Create a relationship between category tags and its names.
	 * 
	 * @param orbeonApplication
	 *            The application name of the form.
	 * @param orbeonFormName
	 *            The form name.
	 * @param orbeonDocumentId
	 *            The document ID of the submitted answers.
	 * @return
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws CategoryNameWithoutTranslation
	 */
	public HashMap<String, String> readXml(String orbeonApplication, String orbeonFormName, IForm form)
			throws DocumentException, MalformedURLException, CategoryNameWithoutTranslation {
		return readXml(form, getXml(orbeonApplication, orbeonFormName));
	}

	public String getCategoryName(IForm form, String categoryTag) throws CategoryNameWithoutTranslation {
		if (formTagsToName.get(form.getId()) != null) {
			return formTagsToName.get(form.getId()).get(categoryTag);
		}
		throw new CategoryNameWithoutTranslation(
				"Category translations not initialized. Call 'readXml()' method first.");
	}

	/**
	 * Update all categories of a form with the user text.
	 * 
	 * @param form
	 * @throws CategoryNameWithoutTranslation
	 *             if any category hasn't be updated.
	 */
	private void updateForm(IForm form) throws CategoryNameWithoutTranslation {
		if (formTagsToName.get(form.getId()) != null) {
			for (ICategory category : form.getCategories()) {
				String text = formTagsToName.get(form.getId()).get(category.getTag());
				if (text != null) {
					category.setText(text);
				} else {
					throw new CategoryNameWithoutTranslation("Category '" + category.getTag() + "' has no translation.");
				}
			}
		} else {
			throw new CategoryNameWithoutTranslation(
					"Category translations not initialized. Call 'readXml()' method first.");
		}
	}

}
