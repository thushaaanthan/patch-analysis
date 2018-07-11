package org.wso2.engineering.efficiency.patch.analysis.api;

/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.wso2.msf4j.security.basic.AbstractBasicAuthSecurityInterceptor;

import java.util.Objects;

/**
 * Authenticating the micro service with Basic Auth via username and password.
 */
public class AuthInterceptor extends AbstractBasicAuthSecurityInterceptor {

    @Override
    protected boolean authenticate(String username, String password) {

        String appUsername = "patch-analysis";
        String appPassword = "BHzR@?CttH=7Q@Sk";
        return Objects.equals(username, appUsername) && Objects.equals(password, appPassword);
    }
}
