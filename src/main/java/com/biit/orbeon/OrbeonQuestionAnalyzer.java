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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class OrbeonQuestionAnalyzer {

    private static Document orbeonFormStructureXml = null;

    private OrbeonQuestionAnalyzer() {

    }

    /**
     * Gets the XML of a orbeon application.
     *
     * @param orbeonApplication The application name of the form.
     * @param orbeonFormName    The form name.
     * @param server            orbeon server IP.
     * @param port              orbeon server port.
     */
    public static void setXmlStructure(String protocol, String server, int port, String orbeonApplication, String orbeonFormName) throws MalformedURLException,
            DocumentException {
        orbeonFormStructureXml = OrbeonImporter.getFormDeclaration(protocol, server, port, orbeonApplication, orbeonFormName);
    }

    public static void setXmlStructure(String xmlText) throws DocumentException {
        final SAXReader xmlReader = new SAXReader();
        final byte[] xmlBytes = xmlText.getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(xmlBytes);
        orbeonFormStructureXml = xmlReader.read(arrayInputStream);
    }

    public static boolean isQuestionMultiSelect(List<String> questionPath) {
        final Node formElement = orbeonFormStructureXml.getRootElement();
        final String categoryName = questionPath.get(0);
        Node resourceSection = formElement.selectSingleNode("//fr:section[@id='" + categoryName + "-control']");
        if (resourceSection != null) {
            // If there are intermediate groups between the category and the
            // question we have to parse them
            if (questionPath.size() > 2) {
                for (int i = 1; i < questionPath.size() - 1; i++) {
                    final String groupName = questionPath.get(i);
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
            final Node questionNode = resourceSection.selectSingleNode("//xf:select[@id='" + questionName + "-control']");
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
