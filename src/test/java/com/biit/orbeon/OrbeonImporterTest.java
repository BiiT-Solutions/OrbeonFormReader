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

import java.net.MalformedURLException;

import org.dom4j.DocumentException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "orbeonImport")
public class OrbeonImporterTest {
	private final static String APP = "WebForms";
	private final static String FORM = "Preview_De_Haagse_Passage_ABCD";
	private final static String DOCUMENT_ID = "a63cec564b2b2d50fd330c9f2412566811598927";

	private final static String SERVER = "localhost";
	private final static int PORT = 8080;
	private final static String PROTOCOL = "http";

	private String xmlText;

	@Test
	public void getXml() throws MalformedURLException, DocumentException {
		xmlText = OrbeonImporter.getXml(PROTOCOL, SERVER, PORT, APP, FORM, DOCUMENT_ID);
		Assert.assertNotNull(xmlText);
	}

}
