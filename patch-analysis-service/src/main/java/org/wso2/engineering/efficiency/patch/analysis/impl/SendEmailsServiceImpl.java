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
package org.wso2.engineering.efficiency.patch.analysis.impl;

import org.apache.log4j.Logger;
import org.wso2.engineering.efficiency.patch.analysis.Configuration;
import org.wso2.engineering.efficiency.patch.analysis.email.EmailContentCreator;
import org.wso2.engineering.efficiency.patch.analysis.email.GmailAccessor;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;

import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Email.MAIN_HEADER_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Email.MAIN_HEADER_END;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Email.MAIN_HEADER_INTERNAL;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Email.SUBJECT_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Email.SUBJECT_INTERNAL;

/**
 * Implementation of the API service to send an email containing Patch data corresponding to active JIRA issues.
 */
public class SendEmailsServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(SendEmailsServiceImpl.class);
    private static SendEmailsServiceImpl sendEmailsService = new SendEmailsServiceImpl();

    public static SendEmailsServiceImpl getInstance() {

        return sendEmailsService;
    }

    /**
     * Update the EE database and send the email.
     * @param isMailOnCustomerIssues determine if its customer or proactive.
     * @throws PatchAnalysisException email was not sent
     */
    public void sendEmail(boolean isMailOnCustomerIssues)
            throws PatchAnalysisException {

        Configuration configuration = Configuration.getInstance();
        ArrayList<JIRAIssue> jiraIssues = UpdateDatabaseServiceImpl.getInstance().updateDB(isMailOnCustomerIssues);
        String emailSubject;
        String emailHeaderHTML;
        if (isMailOnCustomerIssues) {
            emailSubject = SUBJECT_CUSTOMER;
            emailHeaderHTML = MAIN_HEADER_CUSTOMER;
        } else {
            emailSubject = SUBJECT_INTERNAL;
            emailHeaderHTML = MAIN_HEADER_INTERNAL;
        }
        emailHeaderHTML += jiraIssues.size() + MAIN_HEADER_END;
        String emailBodyHTML = EmailContentCreator.getInstance().getEmailBody(jiraIssues, emailHeaderHTML);

        GmailAccessor.getInstance().sendMessage(emailBodyHTML, emailSubject, configuration.getEmailUser(),
                configuration.getToList(), configuration.getCcList());
        LOGGER.info("Successfully sent email with patch information.");
    }
}
