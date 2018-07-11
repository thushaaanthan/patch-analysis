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
package org.wso2.engineering.efficiency.patch.analysis.database;

import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.pmt.Patch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.INACTIVE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.IN_DEV;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.IN_QUEUE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.IN_SIGNING;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.PROACTIVE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.RELEASED;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.STATE_NAME;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.INSERT_JIRA;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.INSERT_JIRA_PATCH;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.INSERT_PATCH;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.INSERT_STATE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_LAST_STATE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SET_JIRAS_AS_RESOLVED;

/**
 * Updates the EE database.
 */
public class DatabaseUpdater {

    private static DatabaseUpdater databaseAccessor = new DatabaseUpdater();

    private DatabaseUpdater() {

    }

    public static DatabaseUpdater getInstance() {

        return databaseAccessor;
    }

    private static java.sql.Timestamp getCurrentTimestamp() {

        java.util.Date date = new java.util.Date();
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * Connect to the EE database and update it.
     *
     * @param jiraIssues                     JIRA issues returned by the JIRA filter.
     * @param isMailOnCustomerReportedIssues is it customer related.
     * @param jiraIssuesInPMT                JIRA issues with a corresponding entry in the PMT.
     * @param dbUser                         database username.
     * @param dbPassword                     database password.
     * @param dbConnection                   database connection.
     */
    public void updateDB(ArrayList<JIRAIssue> jiraIssues, boolean isMailOnCustomerReportedIssues,
                         ArrayList<JIRAIssue> jiraIssuesInPMT, String dbUser, String dbPassword,
                         String dbConnection) throws PatchAnalysisConnectionException {

        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword)) {

            updateJIRATable(jiraIssues, isMailOnCustomerReportedIssues, con);
            updatePatchTable(jiraIssuesInPMT, con);
            updateSTATETable(jiraIssuesInPMT, con);

        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("Could not connect to the Database", e);
        }
    }

    /**
     * Update the JIRA table.
     *
     * @param jiraIssues                     JIRA issues returned by filter.
     * @param isMailOnCustomerReportedIssues mail is customer related or not.
     */
    private void updateJIRATable(ArrayList<JIRAIssue> jiraIssues, boolean isMailOnCustomerReportedIssues,
                                 Connection con) throws PatchAnalysisConnectionException {

        try (PreparedStatement setJIRASAsResolved = con.prepareStatement(SET_JIRAS_AS_RESOLVED);
             PreparedStatement insertToJIRA = con.prepareStatement(INSERT_JIRA)) {
            String jiraType;
            if (isMailOnCustomerReportedIssues) {
                jiraType = CUSTOMER;
            } else {
                jiraType = PROACTIVE;
            }
            setJIRASAsResolved.setString(1, jiraType);
            setJIRASAsResolved.execute();
            for (JIRAIssue jiraIssue : jiraIssues) {
                insertToJIRA.setString(1, jiraIssue.getAssignee());
                insertToJIRA.setString(2, jiraIssue.getLink());
                insertToJIRA.setDate(3, java.sql.Date.valueOf(jiraIssue.getCreateDate()));
                insertToJIRA.setString(4, jiraIssue.getJiraState());
                insertToJIRA.setString(5, jiraType);
                insertToJIRA.addBatch();
            }
            insertToJIRA.executeBatch();
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("JIRA table not updated successfully", e);
        }
    }

    /**
     * Update the Patch table and the JIRA_PATCH table.
     *
     * @param jiraIssuesInPMT JIRA issues that have a corresponding entry in the PMT.
     */
    private void updatePatchTable(ArrayList<JIRAIssue> jiraIssuesInPMT, Connection connection)
            throws PatchAnalysisConnectionException {

        try (PreparedStatement insertPatch = connection.prepareStatement(INSERT_PATCH); PreparedStatement
                insertJIRAPatch = connection.prepareStatement(INSERT_JIRA_PATCH)) {
            for (JIRAIssue jiraIssue : jiraIssuesInPMT) {
                for (Patch patch : jiraIssue.getPatches()) {
                    insertPatch.setString(1, patch.getProductName());
                    insertPatch.setString(2, patch.getPatchLCState());
                    insertPatch.setDate(3, java.sql.Date.valueOf(patch.getReportDate()));
                    insertPatch.setString(4, patch.getName());
                    insertPatch.setInt(5, Integer.parseInt(patch.getPatchQueueId()));
                    if (patch.getSignRequestSentOn() == null) {
                        insertPatch.setNull(6, Types.TIMESTAMP);
                    } else {
                        insertPatch.setTimestamp(6,
                                java.sql.Timestamp.valueOf(patch.getSignRequestSentOn()));
                    }
                    insertPatch.addBatch();
                    insertJIRAPatch.setInt(1, Integer.parseInt(patch.getPatchQueueId()));
                    insertJIRAPatch.setString(2, jiraIssue.getLink());
                    insertJIRAPatch.addBatch();
                }
            }
            insertPatch.executeBatch();
            insertJIRAPatch.executeBatch();
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("PATCH and JIRA_PATCH tables not updated successfully", e);
        }
    }

    /**
     * Update the STATE table.
     *
     * @param jiraIssuesInPMT JIRA issues that have a corresponding entry in the PMT.
     */
    private void updateSTATETable(ArrayList<JIRAIssue> jiraIssuesInPMT, Connection connection)
            throws PatchAnalysisConnectionException {

        try (PreparedStatement insertState = connection.prepareStatement(INSERT_STATE);
             PreparedStatement selectLastState = connection.prepareStatement(SELECT_LAST_STATE)) {
            for (JIRAIssue jiraIssue : jiraIssuesInPMT) {
                for (Patch patch : jiraIssue.getPatches()) {
                    selectLastState.setInt(1, Integer.parseInt(patch.getPatchQueueId()));
                    ResultSet resultSet = selectLastState.executeQuery();
                    //check if patch has entry in State table
                    if (resultSet.next()) {
                        String lastState = resultSet.getString(STATE_NAME);
                        if (checkStateChange(patch, lastState)) {
                            addToBatch(insertState, patch);
                        }
                    } else {
                        addToBatch(insertState, patch);
                    }
                }
            }
            insertState.executeBatch();
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("STATE table not updated successfully", e);
        }
    }

    /**
     * Add entries to be inserted to the STATE table.
     *
     * @param statement prepared statement.
     * @param patch     patch object to be inserted.
     * @throws SQLException could not add Patch data to batch.
     */
    private void addToBatch(PreparedStatement statement, Patch patch) throws SQLException {

        String stateName = "";
        switch (patch.getState()) {
            case IN_DEV:
                stateName = IN_DEV;
                break;
            case IN_PATCH_QUEUE:
                stateName = IN_QUEUE;
                break;
            case IN_SIGNING:
                stateName = IN_SIGNING;
                break;
            case RELEASED:
                stateName = RELEASED;
                break;
            case INACTIVE:
                stateName = INACTIVE;
                break;
            default:
                break;
        }
        statement.setString(1, stateName);
        statement.setTimestamp(2, getCurrentTimestamp());
        statement.setString(3, patch.getPatchQueueId());
        statement.addBatch();
    }

    /**
     * Determine whether a patch has changed state since its last entry in the STATE table.
     *
     * @param patch     Patch
     * @param lastState the last state it was in
     * @return if the state has changed
     */
    private boolean checkStateChange(Patch patch, String lastState) {

        boolean stateChange = true;
        switch (patch.getState()) {
            case IN_DEV:
                if (IN_DEV.equals(lastState)) {
                    stateChange = false;
                }
                break;
            case IN_PATCH_QUEUE:
                if (IN_QUEUE.equals(lastState)) {
                    stateChange = false;
                }
                break;
            case IN_SIGNING:
                if (IN_SIGNING.equals(lastState)) {
                    stateChange = false;
                }
                break;
            case RELEASED:
                if (RELEASED.equals(lastState)) {
                    stateChange = false;
                }
                break;
            case INACTIVE:
                if (INACTIVE.equals(lastState)) {
                    stateChange = false;
                }
                break;
            default:
                break;
        }
        return stateChange;
    }
}
