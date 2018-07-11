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

/**
 * constant values.
 */
public class Constants {

    public static final String JWT_ASSERTION = "X-JWT-Assertion";
    public static final String EMAIL_ADDRESS = "http://wso2.org/claims/emailaddress";
    public static final String ROLE = "http://wso2.org/claims/role";
    static final String MICRO_SERVICE_URL = "micro_service_url";
    static final String MICRO_SERVICE_USERNAME = "micro_service_username";
    static final String MICRO_SERVICE_PASSWORD = "micro_service_password";
    static final String KEYSTORE_FILE_NAME = "sso_keystore_file_name";
    static final String KEYSTORE_PASSWORD = "sso_keystore_password";
    static final String CERTIFICATE_ALIAS = "sso_certificate_alias";
    static final String SSO_REDIRECT_URL = "sso_redirect_url";
    static final String ALLOWED_USER_ROLE = "allowed_user_role";
    static final String TRUST_STORE_SERVICE_NAME = "trust_store_service_name";
    static final String TRUST_STORE_SERVICE_PASSWORD = "trust_store_service_password";
    static final String DATABASE_UPDATE_ALLOWED_USER_ROLES = "database_update_allowed_user_roles";
    public static final String DATABASE_UPDATE_URL = "/database";
}
