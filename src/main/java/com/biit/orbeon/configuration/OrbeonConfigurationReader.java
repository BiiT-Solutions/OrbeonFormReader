package com.biit.orbeon.configuration;

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

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public final class OrbeonConfigurationReader extends ConfigurationReader {
    private static final String CONFIG_FILE = "settings.conf";
    private static final String SYSTEM_VARIABLE_CONFIG = "ORBEON_IMPORTER_CONFIG";

    // Liferay Profile
    private static final String ORBEON_SERVER_TAG = "orbeon.server";
    private static final String ORBEON_SERVER_PORT = "orbeon.port";
    private static final String ORBEON_SERVER_PROTOCOL = "orbeon.protocol";
    private static final String ORBEON_LANGUAGE = "orbeon.language";

    private static final String DEFAULT_ORBEON_SERVER = "localhost";
    private static final int DEFAULT_ORBEON_PORT = 8080;
    private static final String DEFAULT_ORBEON_PROTOCOL = "http";
    private static final String DEFAULT_ORBEON_LANGUAGE = "en";

    private static OrbeonConfigurationReader instance;

    private OrbeonConfigurationReader() {
        super();

        addProperty(ORBEON_SERVER_TAG, DEFAULT_ORBEON_SERVER);
        addProperty(ORBEON_SERVER_PORT, DEFAULT_ORBEON_PORT);
        addProperty(ORBEON_SERVER_PROTOCOL, DEFAULT_ORBEON_PROTOCOL);
        addProperty(ORBEON_LANGUAGE, DEFAULT_ORBEON_LANGUAGE);

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

    public String getOrbeonLanguage() {
        return getPropertyLogException(ORBEON_LANGUAGE);
    }

}
