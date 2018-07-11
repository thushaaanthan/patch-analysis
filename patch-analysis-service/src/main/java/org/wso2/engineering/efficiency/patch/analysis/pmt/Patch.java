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

package org.wso2.engineering.efficiency.patch.analysis.pmt;

import org.wso2.engineering.efficiency.patch.analysis.email.HtmlTableRow;
import org.wso2.engineering.efficiency.patch.analysis.utils.State;

/**
 * A patch associated with a JIRA issue
 */
public class Patch implements HtmlTableRow {

    private String jiraLink;
    private String name;
    private String productName;
    private String assignee;
    private State state;
    private String patchLCState;
    private String patchQueueId;
    private String reportDate;
    private String signRequestSentOn;

    Patch(String jiraLink, String name, String productName, String assignee, State state,
          String patchLCState, String patchQueueId, String reportDate, String signRequestSentOn) {

        this.jiraLink = jiraLink;
        this.name = name;
        this.productName = productName;
        this.assignee = assignee;
        this.state = state;
        this.patchLCState = patchLCState;
        this.patchQueueId = patchQueueId;
        this.reportDate = reportDate;
        this.signRequestSentOn = signRequestSentOn;
    }

    public String getSignRequestSentOn() {

        return signRequestSentOn;
    }

    public String getPatchQueueId() {

        return patchQueueId;
    }

    public Integer getDaysInState() {

        return 0;
    }

    public String getName() {

        return name;
    }

    public State getState() {

        return state;
    }

    public String getPatchLCState() {

        return this.patchLCState;
    }

    String getJiraLink() {

        return jiraLink;
    }

    public String getProductName() {

        return productName;
    }

    String getAssignee() {

        return assignee;
    }

    @Override
    public String objectToHTML(String backgroundColor) {

        return null;
    }

    public String getReportDate() {

        return reportDate;
    }
}
