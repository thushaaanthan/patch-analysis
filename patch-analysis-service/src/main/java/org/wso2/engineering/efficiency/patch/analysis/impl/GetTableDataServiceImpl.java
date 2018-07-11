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

import org.wso2.engineering.efficiency.patch.analysis.Configuration;
import org.wso2.engineering.efficiency.patch.analysis.database.DatabaseAccessor;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;

import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_DEV_PATCHES;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_RELEASED_PATCHES;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_SIGNING_PATCHES;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.PROACTIVE;

/**
 * Implementation of the API services to get JIRA and Patch data from the EE database.
 */
public class GetTableDataServiceImpl {

    private static GetTableDataServiceImpl getTableDataService = new GetTableDataServiceImpl();

    public static GetTableDataServiceImpl getInstance() {

        return getTableDataService;
    }

    public String getDevPatchData(String issueType) throws PatchAnalysisException {

        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getPatches(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), SELECT_DEV_PATCHES, issueType);
    }

    public String getInactivePatchData(String issueType) throws PatchAnalysisException {

        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getInactivePatches(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), issueType).toString();
    }

    public String getSigningPatchData(String issueType) throws PatchAnalysisException {

        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getPatches(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), SELECT_SIGNING_PATCHES, issueType);
    }

    public String getReleasedPatchData(String issueType) throws PatchAnalysisException {
        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getPatches(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), SELECT_RELEASED_PATCHES, issueType);
    }

    public String getSummaryPatchData(String issueType) throws PatchAnalysisException {
        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getSummaryInformation(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), issueType);
    }

    public String getJIRACount(String issueType) throws PatchAnalysisException {

        checkIssueType(issueType);
        Configuration configuration = Configuration.getInstance();
        return DatabaseAccessor.getInstance().getJIRACount(configuration.getEEOPUser(),
                configuration.getEEOPPassword(), configuration.getEEOPConnection(), issueType);
    }

    /**
     * Is the JIRA issue type one of customer or proactive.
     * @param issueType type of JIRA issue
     * @throws PatchAnalysisException incorrect parameter passed.
     */
    private void checkIssueType(String issueType) throws PatchAnalysisException {
        if (!(CUSTOMER.equals(issueType) || PROACTIVE.equals(issueType))) {
            throw new PatchAnalysisException("Issue type should be 'customer' or 'proactive'");
        }
    }
}
