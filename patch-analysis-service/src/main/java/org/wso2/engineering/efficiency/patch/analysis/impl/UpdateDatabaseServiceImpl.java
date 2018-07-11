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
import org.wso2.engineering.efficiency.patch.analysis.database.DatabaseUpdater;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAAccessor;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.pmt.PMTAccessor;

import java.util.ArrayList;

/**
 * Implementation of the API service tp update the EE DB.
 */
public class UpdateDatabaseServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(UpdateDatabaseServiceImpl.class);
    private static UpdateDatabaseServiceImpl updateDatabaseService = new UpdateDatabaseServiceImpl();

    public static UpdateDatabaseServiceImpl getInstance() {

        return updateDatabaseService;
    }

    public ArrayList<JIRAIssue> updateDB(boolean isMailOnCustomerIssues)
            throws PatchAnalysisException {

        Configuration configuration = Configuration.getInstance();
        //access JIRA
        ArrayList<JIRAIssue> jiraIssues = accessJIRA(isMailOnCustomerIssues, configuration.getUrlToJIRAFilterCustomer(),
                configuration.getUrlToJIRAFilterInternal(), configuration.getJiraAuthentication());
        //access PMT
        ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = accessPMT(configuration.getPmtConnection(),
                configuration.getPmtUser(), configuration.getPmtPassword(), jiraIssues);
        //update EE DB
        DatabaseUpdater.getInstance().updateDB(jiraIssues, isMailOnCustomerIssues, jiraIssuesInPmtAndJIRA,
                configuration.getEEOPUser(), configuration.getEEOPPassword(), configuration.getEEOPConnection());
        return jiraIssues;
    }

    /**
     * Get the JIRA issues returned by the JIRA filter.
     *
     * @param isMailOnCustomerIssues is it customer related.
     * @param jiraAuthentication     Basic authentication.
     * @return JIRA issues.
     * @throws PatchAnalysisException could not extract JIRA issues.
     */
    private ArrayList<JIRAIssue> accessJIRA(boolean isMailOnCustomerIssues, String urlToJIRAFilterCustomer,
                                            String urlToJIRAFilterInternal, String jiraAuthentication)
            throws PatchAnalysisException {

        String urlToJIRAFilter;
        if (isMailOnCustomerIssues) {
            urlToJIRAFilter = urlToJIRAFilterCustomer;
        } else {
            urlToJIRAFilter = urlToJIRAFilterInternal;
        }
        try {
            ArrayList<JIRAIssue> jiraIssues;
            jiraIssues = new ArrayList<>(JIRAAccessor.getInstance().getIssues(urlToJIRAFilter, jiraAuthentication));
            LOGGER.info("Successfully extracted data from JIRA.");
            return jiraIssues;

        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to extract data from JIRA.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }

    /**
     * Assign Patches to JIRA issues.
     *
     * @param pmtConnection   pmt connection.
     * @param pmtUserName     pmt username.
     * @param pmtUserPassword pmt password.
     * @param jiraIssues      JIRA issues returned by the JIRA filter.
     * @throws PatchAnalysisException Could not extract Patch information from pmt.
     */
    private ArrayList<JIRAIssue> accessPMT(String pmtConnection, String pmtUserName, String pmtUserPassword,
                                           ArrayList<JIRAIssue> jiraIssues) throws PatchAnalysisException {

        try {
            PMTAccessor pmtAccessor = PMTAccessor.getInstance();
            ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = new ArrayList<>(
                    pmtAccessor.filterJIRAIssues(jiraIssues, pmtConnection, pmtUserName, pmtUserPassword));
            pmtAccessor.populatePatches(jiraIssuesInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword);
            LOGGER.info("Successfully extracted data from the PMT.");
            return jiraIssuesInPmtAndJIRA;
        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to extract data from the PMT.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }
}
