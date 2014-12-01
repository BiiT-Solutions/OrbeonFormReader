package com.biit.orbeon;

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
