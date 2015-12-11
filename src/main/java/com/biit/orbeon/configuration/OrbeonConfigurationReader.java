package com.biit.orbeon.configuration;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public class OrbeonConfigurationReader extends ConfigurationReader {
	private final String CONFIG_FILE = "settings.conf";
	private static final String SYSTEM_VARIABLE_CONFIG = "ORBEON_IMPORTER_CONFIG";

	// Liferay Profile
	private final String ORBEON_SERVER_TAG = "orbeon.server";
	private final String ORBEON_SERVER_PORT = "orbeon.port";
	private final String ORBEON_SERVER_PROTOCOL = "orbeon.protocol";

	private final String DEFAULT_ORBEON_SERVER = "localhost";
	private final int DEFAULT_ORBEON_PORT = 8080;
	private final String DEFAULT_ORBEON_PROTOCOL = "http";

	private static OrbeonConfigurationReader instance;

	private OrbeonConfigurationReader() {
		super();

		addProperty(ORBEON_SERVER_TAG, DEFAULT_ORBEON_SERVER);
		addProperty(ORBEON_SERVER_PORT, DEFAULT_ORBEON_PORT);
		addProperty(ORBEON_SERVER_PROTOCOL, DEFAULT_ORBEON_PROTOCOL);

		addPropertiesSource(new PropertiesSourceFile(CONFIG_FILE));
		addPropertiesSource(new SystemVariablePropertiesSourceFile(SYSTEM_VARIABLE_CONFIG, CONFIG_FILE));

		readConfigurations();
	}

	public static OrbeonConfigurationReader getInstance() {
		if (instance == null) {
			synchronized (OrbeonConfigurationReader.class) {
				if (instance == null) {
					instance = new OrbeonConfigurationReader();
				}
			}
		}
		return instance;
	}

	private String getPropertyLogException(String propertyId) {
		try {
			return getProperty(propertyId);
		} catch (PropertyNotFoundException e) {
			BiitCommonLogger.errorMessageNotification(OrbeonConfigurationReader.class, e);
			return null;
		}
	}

	public String getOrbeonServer() {
		return getPropertyLogException(ORBEON_SERVER_TAG);
	}

	public int getOrbeonPort() {
		try {
			return Integer.parseInt(getPropertyLogException(ORBEON_SERVER_PORT));
		} catch (Exception e) {
			BiitCommonLogger.errorMessageNotification(OrbeonConfigurationReader.class, e);
			return DEFAULT_ORBEON_PORT;
		}

	}

	public String getOrbeonProtocol() {
		return getPropertyLogException(ORBEON_SERVER_PROTOCOL);
	}

}
