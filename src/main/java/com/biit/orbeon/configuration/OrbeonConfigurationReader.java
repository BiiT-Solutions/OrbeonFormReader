package com.biit.orbeon.configuration;

import java.io.IOException;
import java.util.Properties;

import com.biit.utils.file.PropertiesFile;

public class OrbeonConfigurationReader {
	private final String DATABASE_CONFIG_FILE = "settings.conf";

	// Liferay Profile
	private final String ORBEON_SERVER_TAG = "orbeonServer";
	private final String ORBEON_SERVER_PORT = "orbeonPort";

	private final String DEFAULT_ORBEON_SERVER = "localhost";
	private final int DEFAULT_ORBEON_PORT = 8080;

	private String orbeonServer;
	private Integer orbeonPort;

	private static OrbeonConfigurationReader instance;

	private OrbeonConfigurationReader() {
		readConfig();
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

	/**
	 * Read database config from resource and update default connection parameters.
	 */
	private void readConfig() {
		Properties prop = new Properties();
		try {
			prop = PropertiesFile.load(DATABASE_CONFIG_FILE);
			orbeonServer = prop.getProperty(ORBEON_SERVER_TAG);
			try {
				orbeonPort = Integer.parseInt(prop.getProperty(ORBEON_SERVER_PORT));
			} catch (Exception e) {
				// Do nothing.
			}
		} catch (IOException e) {
			// Do nothing.
		}

		if (orbeonServer == null) {
			orbeonServer = DEFAULT_ORBEON_SERVER;
		}

		if (orbeonPort == null) {
			orbeonPort = DEFAULT_ORBEON_PORT;
		}
	}

	public String getOrbeonServer() {
		return orbeonServer;
	}

	public int getOrbeonPort() {
		return orbeonPort;
	}

}
