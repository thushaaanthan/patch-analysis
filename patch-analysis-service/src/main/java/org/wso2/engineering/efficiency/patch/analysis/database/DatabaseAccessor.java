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

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisDataException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.JIRA_ASSIGNEE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.JIRA_CREATE_DATE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.JIRA_NAME;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.LC_STATE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.NA;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.NO_ENTRY_IN_PMT;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.OPEN_PATCHES;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.PATCH_NAME;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.EE.PRODUCT_NAME;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_JIRAS_NOT_RECORDED;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_JIRAS_WITH_NO_ACTIVE_PATCHES;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_NUM_JIRAS;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.SQLStatement.SELECT_SUMMARY_OF_ACTIVE_PATCHES;

/**
 * Get JIRA and Patch data from the EE database.
 */
public class DatabaseAccessor {

    private static DatabaseAccessor databaseAccessor = new DatabaseAccessor();

    public static DatabaseAccessor getInstance() {

        return databaseAccessor;
    }

    /**
     * Select Patches corresponding to open JIRA issues (for a predefined state).
     * @param dbUser EE username
     * @param dbPassword EE password
     * @param dbConnection EE connection
     * @param query mysql select prepared statement
     * @param issueType type of JIRA issues (customer or proactive).
     * @return patch information in JSON
     * @throws PatchAnalysisException patch data not extracted
     */
    public String getPatches(String dbUser, String dbPassword,
                             String dbConnection, String query, String issueType) throws PatchAnalysisException {

        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, issueType);
            ResultSet result = statement.executeQuery();
            JSONArray jsonArray = new JSONArray();
            return addPatches(result, jsonArray).toString();
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("Data not extracted successfully", e);
        }
    }

    /**
     * Add Patch data returned by resultSet to the JSON Array as JSON objects.
     * @param result resultSet returned by query.
     * @param jsonArray to store JSON objects.
     * @return JSON array with additional Patch data.
     * @throws PatchAnalysisDataException ResultSet not converted to JSON.
     */
    private JSONArray addPatches(ResultSet result, JSONArray jsonArray) throws PatchAnalysisDataException {

        try {
            while (result.next()) {
                int columnCount = result.getMetaData().getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < columnCount; i++) {
                    jsonObject.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(),
                            result.getObject(i + 1));
                }
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        } catch (SQLException e) {
            throw new PatchAnalysisDataException("ResultSet not converted to JSON.", e);
        }
    }

    /**
     * Select inactive Patches corresponding to open JIRA issues.
     * @param dbUser EE username
     * @param dbPassword EE password
     * @param dbConnection EE connection
     * @param issueType type of JIRA issues (customer or proactive).
     * @return String containing Patch information in JSON
     * @throws PatchAnalysisException Inactive Patch data not extracted.
     */
    public JSONArray getInactivePatches(String dbUser, String dbPassword, String dbConnection, String issueType)
            throws PatchAnalysisException {

        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
             PreparedStatement statement = con.prepareStatement(SELECT_JIRAS_NOT_RECORDED);
             PreparedStatement selectJIRASWithNoActivePatches =
                     con.prepareStatement(SELECT_JIRAS_WITH_NO_ACTIVE_PATCHES)) {
            statement.setString(1, issueType);
            ResultSet result = statement.executeQuery();
            JSONArray jsonArray = new JSONArray();
            while (result.next()) {
                int columnCount = result.getMetaData().getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < columnCount; i++) {
                    jsonObject.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(),
                            result.getObject(i + 1));
                }
                jsonObject.put(PATCH_NAME, NO_ENTRY_IN_PMT);
                jsonObject.put(PRODUCT_NAME, NA);
                jsonObject.put(LC_STATE, NA);
                jsonArray.put(jsonObject);
            }
            selectJIRASWithNoActivePatches.setString(1, issueType);
            return addPatches(selectJIRASWithNoActivePatches.executeQuery(), jsonArray);
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("Inactive Patch data not extracted from DB", e);
        }
    }

    /**
     * Select Patches corresponding to all open JIRA issues.
     * @param dbUser EE username
     * @param dbPassword EE password
     * @param dbConnection EE connection
     * @param issueType type of JIRA issues (customer or proactive).
     * @return String containing Patch information in JSON.
     * @throws PatchAnalysisException Summary data not extracted.
     */
    public String getSummaryInformation(String dbUser, String dbPassword, String dbConnection, String issueType)
            throws PatchAnalysisException {

        JSONArray jsonArray = new JSONArray();
        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
             PreparedStatement statement = con.prepareStatement(SELECT_SUMMARY_OF_ACTIVE_PATCHES)) {
            statement.setString(1, issueType);
            addPatches(statement.executeQuery(), jsonArray);
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("Summary Data not extracted from DB.", e);
        }
        //add inactive JIRAs
        JSONArray inactivePatches = getInactivePatches(dbUser, dbPassword, dbConnection, issueType);
        for (int i = 0; i < inactivePatches.length(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JIRA_NAME, inactivePatches.getJSONObject(i).get(JIRA_NAME));
            jsonObject.put(JIRA_ASSIGNEE, inactivePatches.getJSONObject(i).get(JIRA_ASSIGNEE));
            jsonObject.put(OPEN_PATCHES, "0");
            jsonObject.put(JIRA_CREATE_DATE, inactivePatches.getJSONObject(i).get(JIRA_CREATE_DATE));
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     *
     * @param dbUser EE username.
     * @param dbPassword EE password.
     * @param dbConnection EE connection.
     * @param issueType type of JIRA issues (customer or proactive).
     * @return String containing number of active JIRA issues.
     * @throws PatchAnalysisConnectionException count not extracted.
     */
    public String getJIRACount(String dbUser, String dbPassword, String dbConnection, String issueType)
            throws PatchAnalysisConnectionException {

        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
             PreparedStatement statement = con.prepareStatement(SELECT_NUM_JIRAS)) {
            statement.setString(1, issueType);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getString(1);
        } catch (SQLException e) {
            throw new PatchAnalysisConnectionException("Total JIRA count not extracted from DB.", e);
        }
    }
}
