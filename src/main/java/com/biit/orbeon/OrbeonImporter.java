package com.biit.orbeon;

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
import com.biit.form.submitted.implementation.SubmittedObject;
import com.biit.logger.BiitCommonLogger;
import com.biit.orbeon.configuration.OrbeonConfigurationReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Reads data from Orbeon Form.
 */
public abstract class OrbeonImporter {

    private static final String REPEATABLE_GROUP_SUFIX = "-iterator";

    /**
     * Read the form answers of a orbeon form.
     *
     * @param orbeonApplication The application name of the form.
     * @param orbeonFormName    The form name.
     * @param orbeonDocumentId  The document ID of the submitted answers.
     * @return a form with all user answers.
     * @throws MalformedURLException
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public ISubmittedForm readFormAnswers(String orbeonApplication, String orbeonFormName, String orbeonDocumentId) throws MalformedURLException,
            DocumentException, UnsupportedEncodingException {
        final ISubmittedForm form = createForm(orbeonApplication, orbeonFormName);
        readXml(getXml(orbeonApplication, orbeonFormName, orbeonDocumentId), form);
        return form;
    }

    /**
     * Read the form answers of a orbeon form.
     *
     * @param form             The form.
     * @param orbeonDocumentId The document ID of the submitted answers.
     * @return a form with all user answers.
     * @throws MalformedURLException
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public ISubmittedForm readFormAnswers(ISubmittedForm form, String orbeonDocumentId) throws MalformedURLException, DocumentException,
            UnsupportedEncodingException {
        readXml(getXml(form.getApplicationName(), form.getName(), orbeonDocumentId), form);
        return form;
    }

    /**
     * Read the form answers of an orbeon form.
     *
     * @param server            The IP of the server that hosts the orbeon web application.
     * @param port              The port of the server that hosts the orbeon web application.
     * @param orbeonApplication The application name of the form.
     * @param orbeonFormName    The form name.
     * @param orbeonDocumentId  The document ID of the submitted answers.
     * @return a form with all user answers.
     * @throws MalformedURLException
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public ISubmittedForm readFormAnswers(String protocol, String server, int port, String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
            throws MalformedURLException, DocumentException, UnsupportedEncodingException {
        return getFormFromXml(getXml(protocol, server, port, orbeonApplication, orbeonFormName, orbeonDocumentId), orbeonApplication, orbeonFormName);
    }

    public ISubmittedForm readFormAnswers(String url, String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
            throws MalformedURLException, DocumentException, UnsupportedEncodingException {
        return getFormFromXml(getXml(url, orbeonApplication, orbeonFormName, orbeonDocumentId), orbeonApplication, orbeonFormName);
    }

    /**
     * Read the form answers of an orbeon form.
     *
     * @param xml               xmls content from orbeon server.
     * @param orbeonApplication application name of the form.
     * @param orbeonFormName    name of the form.
     * @return a form structured as a Submitted Form.
     * @throws UnsupportedEncodingException
     * @throws DocumentException
     */
    public ISubmittedForm getFormFromXml(String xml, String orbeonApplication, String orbeonFormName) throws UnsupportedEncodingException, DocumentException {
        final ISubmittedForm form = createForm(orbeonApplication, orbeonFormName);
        readXml(xml, form);
        return form;
    }

    public void setOrbeonStructure(String xmlText) throws DocumentException {
        OrbeonQuestionAnalyzer.setXmlStructure(xmlText);
    }

    /**
     * Gets the XML of a orbeon application.
     *
     * @param orbeonApplication The application name of the form.
     * @param orbeonFormName    The form name.
     * @param orbeonDocumentId  The document ID of the submitted answers.
     * @return a form with all user answers.
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public static String getXml(String orbeonApplication, String orbeonFormName, String orbeonDocumentId) throws MalformedURLException, DocumentException {
        return getXml(OrbeonConfigurationReader.getInstance().getOrbeonProtocol(), OrbeonConfigurationReader.getInstance().getOrbeonServer(),
                OrbeonConfigurationReader.getInstance().getOrbeonPort(), orbeonApplication, orbeonFormName, orbeonDocumentId);
    }

    /**
     * Gets the XML of an orbeon application.
     *
     * @param orbeonApplication The application name of the form.
     * @param orbeonFormName    The form name.
     * @param orbeonDocumentId  The document ID of the submitted answers.
     * @param server orbeon server IP.
     * @param port orbeon server port.
     * @return a form with all user answers.
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public static String getXml(String protocol, String server, int port, String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
            throws MalformedURLException, DocumentException {
        // Get the orbeon document structure
        final Document formAsXml = getFormDeclaration(protocol, server, port, orbeonApplication, orbeonFormName);
        if (formAsXml == null) {
            throw new DocumentException("Orbeon form '" + orbeonFormName + "' is invalid.");
        }
        OrbeonQuestionAnalyzer.setXmlStructure(formAsXml.asXML());
        // Get the submitted document
        final String xmlURL = protocol + "://" + server + ":" + port + "/orbeon/fr/service/persistence/crud/"
                + orbeonApplication + "/" + orbeonFormName + "/data/"
                + orbeonDocumentId + "/data.xml";
        BiitCommonLogger.debug(OrbeonImporter.class, "Accessing to: " + xmlURL);
        final SAXReader xmlReader = new SAXReader();

        final Document xmlResponse = xmlReader.read(new URL(xmlURL));
        if (xmlResponse != null) {
            return xmlResponse.asXML();
        }
        return null;
    }

    public static String getXml(String url, String orbeonApplication, String orbeonFormName, String orbeonDocumentId)
            throws MalformedURLException, DocumentException {
        // Get the orbeon document structure
        final Document formAsXml = getFormDeclaration(url, orbeonApplication, orbeonFormName);
        if (formAsXml == null) {
            throw new DocumentException("Orbeon form '" + orbeonFormName + "' is invalid.");
        }
        OrbeonQuestionAnalyzer.setXmlStructure(formAsXml.asXML());
        // Get the submitted document
        final String xmlURL = url + "/fr/service/persistence/crud/" + orbeonApplication + "/" + orbeonFormName + "/data/"
                + orbeonDocumentId + "/data.xml";
        BiitCommonLogger.debug(OrbeonImporter.class, "Accessing to: " + xmlURL);
        final SAXReader xmlReader = new SAXReader();

        final Document xmlResponse = xmlReader.read(new URL(xmlURL));
        if (xmlResponse != null) {
            return xmlResponse.asXML();
        }
        return null;
    }

    /**
     * Gets the XML that defines an Orbeon form.
     *
     * @param protocol          http/https
     * @param server            server name or IP
     * @param port              port of the server
     * @param orbeonApplication Usually "WebForms"
     * @param orbeonFormName    The name of the form.
     * @return
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public static Document getFormDeclaration(String protocol, String server, int port, String orbeonApplication, String orbeonFormName)
            throws MalformedURLException, DocumentException {
        // Get the document structure
        final String xmlURL = protocol + "://" + server + ":" + port + "/orbeon/fr/service/persistence/crud/" + orbeonApplication + "/" + orbeonFormName
                + "/form/form.xhtml";
        BiitCommonLogger.debug(OrbeonImporter.class, "Accessing to: " + xmlURL);
        final SAXReader xmlReader = new SAXReader();

        return xmlReader.read(new URL(xmlURL));
    }

    public static Document getFormDeclaration(String url, String orbeonApplication, String orbeonFormName)
            throws MalformedURLException, DocumentException {
        // Get the document structure
        final String xmlURL = url + "/fr/service/persistence/crud/" + orbeonApplication + "/" + orbeonFormName
                + "/form/form.xhtml";
        BiitCommonLogger.debug(OrbeonImporter.class, "Accessing to: " + xmlURL);
        final SAXReader xmlReader = new SAXReader();

        return xmlReader.read(new URL(xmlURL));
    }

    /**
     * Adds the user submitted answers into a Form object.
     *
     * @param xmlText
     * @param form
     */
    @SuppressWarnings("rawtypes")
    public void readXml(String xmlText, ISubmittedForm form) throws DocumentException, UnsupportedEncodingException {
        if (xmlText == null) {
            return;
        }
        final SAXReader xmlReader = new SAXReader();
        final Document xmlResponse = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
        final Element formElement = xmlResponse.getRootElement();

        final List<String> questionPath = new ArrayList<>();
        for (Iterator formChildren = formElement.elementIterator(); formChildren.hasNext();) {
            final Element xmlCategory = (Element) formChildren.next();
            // Hide email.
            if (!xmlCategory.getName().equals("liferay_email_address")) {
                // With each category we restart the orbeon path
                questionPath.clear();
                questionPath.add(xmlCategory.getName());
                final ISubmittedCategory category = createCategory(form, xmlCategory.getName());
                form.addChild((SubmittedObject) category);
                readGroups(xmlCategory, category, questionPath);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void readGroups(Element xmlParentGroup, ISubmittedObject parent, List<String> questionPath) {
        for (Iterator children = xmlParentGroup.elementIterator(); children.hasNext();) {
            final Element xmlGroupOrQuestion = (Element) children.next();
            // If it has nested elements, is a group.
            if (xmlGroupOrQuestion.elementIterator().hasNext()) {
                // If it has a "-iterator" is a repeatable group.
                if (xmlGroupOrQuestion.getName().endsWith(REPEATABLE_GROUP_SUFIX)) {
                    ((ISubmittedGroup) parent).increaseNumberOfIterations();
                    // Ignore dummy iterator group and put questions in the
                    // parent
                    readGroups(xmlGroupOrQuestion, parent, questionPath);
                } else {
                    questionPath.add(xmlGroupOrQuestion.getName());
                    final ISubmittedGroup group = createGroup(parent, xmlGroupOrQuestion.getName());
                    parent.addChild((SubmittedObject) group);
                    readGroups(xmlGroupOrQuestion, group, questionPath);
                    questionPath.remove(questionPath.size() - 1);
                }
            } else {
                questionPath.add(xmlGroupOrQuestion.getName());
                final ISubmittedQuestion question = createQuestion(parent, xmlGroupOrQuestion.getName());
                parent.addChild((SubmittedObject) question);
                if (OrbeonQuestionAnalyzer.isStructureSet() && OrbeonQuestionAnalyzer.isQuestionMultiSelect(questionPath)) {
                    final String[] answers = xmlGroupOrQuestion.getText().split(" ");
                    question.setAnswers(new HashSet<String>(Arrays.asList(answers)));
                } else {
                    question.addAnswer(xmlGroupOrQuestion.getText());
                }
                questionPath.remove(questionPath.size() - 1);
            }
        }
    }

    public abstract ISubmittedForm createForm(String applicationName, String formName);

    public abstract ISubmittedCategory createCategory(ISubmittedObject parent, String tag);

    public abstract ISubmittedGroup createGroup(ISubmittedObject parent, String tag);

    public abstract ISubmittedQuestion createQuestion(ISubmittedObject parent, String tag);
}
