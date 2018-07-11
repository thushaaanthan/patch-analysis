/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.wso2.engineering.patchanalysis.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read the properties file.
 */
public class PropertyReader {

    private static final Logger log = LoggerFactory.getLogger(PropertyReader.class);
    private static final String configFileName = "config.properties";
    private String microServiceUrl;
    private String microServicePassword;
    private String microServiceUsername;
    private String ssoKeyStoreName;
    private String ssoKeyStorePassword;
    private String ssoCertAlias;
    private String allowedUserRole;
    private String ssoRedirectUrl;
    private String trustStoreServiceName;
    private String trustStoreServicePassword;
    private String databaseUpdateAllowedUserRoles;

    public PropertyReader() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName);
        loadConfigs(inputStream);
    }

    /**
     * Load config values from the file
     *
     * @param input - input stream of the file
     */
    private void loadConfigs(InputStream input) {

        Properties prop = new Properties();
        try {
            prop.load(input);
            this.microServiceUrl = prop.getProperty(Constants.MICRO_SERVICE_URL);
            this.microServiceUsername = prop.getProperty(Constants.MICRO_SERVICE_USERNAME);
            this.microServicePassword = prop.getProperty(Constants.MICRO_SERVICE_PASSWORD);
            this.ssoKeyStoreName = prop.getProperty(Constants.KEYSTORE_FILE_NAME);
            this.ssoKeyStorePassword = prop.getProperty(Constants.KEYSTORE_PASSWORD);
            this.ssoCertAlias = prop.getProperty(Constants.CERTIFICATE_ALIAS);
            this.ssoRedirectUrl = prop.getProperty(Constants.SSO_REDIRECT_URL);
            this.allowedUserRole = prop.getProperty(Constants.ALLOWED_USER_ROLE);
            this.trustStoreServiceName = prop.getProperty(Constants.TRUST_STORE_SERVICE_NAME);
            this.trustStoreServicePassword = prop.getProperty(Constants.TRUST_STORE_SERVICE_PASSWORD);
            this.databaseUpdateAllowedUserRoles = prop.getProperty(Constants.DATABASE_UPDATE_ALLOWED_USER_ROLES);

        } catch (FileNotFoundException e) {
            log.error("The configuration file is not found");
        } catch (IOException e) {
            log.error("The File cannot be read");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("The File InputStream is not closed");
                }
            }
        }

    }

    public String getMicroServiceUrl() {

        return this.microServiceUrl;
    }

    public String getMicroServiceUsername() {

        return this.microServiceUsername;
    }

    public String getMicroServicePassword() {

        return this.microServicePassword;
    }

    public String getSSOKeyStoreName() {

        return this.ssoKeyStoreName;
    }

    public String getSSOKeyStorePassword() {

        return this.ssoKeyStorePassword;
    }

    public String getSSOCertAlias() {

        return this.ssoCertAlias;
    }

    public String getSSORedirectUrl() {

        return this.ssoRedirectUrl;
    }

    public String getAllowedUserRole() {

        return this.allowedUserRole;
    }

    public String getTrustStoreServiceName() {

        return trustStoreServiceName;
    }

    public String getTrustStoreServicePassword() {

        return trustStoreServicePassword;
    }

    public String[] getDatabaseUpdateAllowedUserRoles() {

        return databaseUpdateAllowedUserRoles.split(",");
    }

}
