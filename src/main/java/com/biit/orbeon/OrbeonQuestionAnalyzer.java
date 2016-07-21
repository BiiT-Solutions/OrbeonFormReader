package com.biit.orbeon;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class OrbeonQuestionAnalyzer {

	private static Document orbeonFormStructureXml = null;

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
	public static void setXmlStructure(String protocol, String server, int port, String orbeonApplication, String orbeonFormName) throws MalformedURLException,
			DocumentException {
		orbeonFormStructureXml = OrbeonImporter.getFormDeclaration(protocol, server, port, orbeonApplication, orbeonFormName);
	}

	public static void setXmlStructure(String xmlText) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		byte[] xmlBytes = xmlText.getBytes(StandardCharsets.UTF_8);
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(xmlBytes);
		orbeonFormStructureXml = xmlReader.read(arrayInputStream);
	}

	public static boolean isQuestionMultiSelect(List<String> questionPath) {
		final Node formElement = orbeonFormStructureXml.getRootElement();
		String categoryName = questionPath.get(0);
		Node resourceSection = formElement.selectSingleNode("//fr:section[@id='" + categoryName + "-control']");
		if (resourceSection != null) {
			// If there are intermediate groups between the category and the
			// question we have to parse them
			if (questionPath.size() > 2) {
				for (int i = 1; i < questionPath.size() - 1; i++) {
					String groupName = questionPath.get(i);
					resourceSection = resourceSection.selectSingleNode("//fr:section[@id='" + groupName + "-control']");
					// Does not exist the group or category, do not try to
					// search for a question.
					if (resourceSection == null) {
						break;
					}
				}
				if (resourceSection != null) {
					return isQuestionMultiSelect(resourceSection, questionPath.get(questionPath.size() - 1));
				}
			} else {
				return isQuestionMultiSelect(resourceSection, questionPath.get(1));
			}
		}
		return false;
	}

	private static boolean isQuestionMultiSelect(Node resourceSection, String questionName) {
		if (resourceSection != null) {
			// xf:select means it's a multi selection node
			// xf:select1 means it's a simple selection node
			Node questionNode = resourceSection.selectSingleNode("//xf:select[@id='" + questionName + "-control']");
			// If the node is found the question is multi select
			if (questionNode != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean isStructureSet() {
		if (orbeonFormStructureXml == null) {
			return false;
		}
		return true;
	}
}
