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
package org.wso2.engineering.efficiency.patch.analysis.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.impl.GetTableDataServiceImpl;
import org.wso2.engineering.efficiency.patch.analysis.impl.SendEmailsServiceImpl;
import org.wso2.engineering.efficiency.patch.analysis.impl.UpdateDatabaseServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.ERROR;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.RESPONSE_MESSAGE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.RESPONSE_TYPE;
import static org.wso2.engineering.efficiency.patch.analysis.utils.Constants.Service.SUCCESS;
/**
 * Main Service class which contains all the micro service endpoints.
 */
@Path("/patchAnalysis")
public class Service {

    private static final Logger LOGGER = Logger.getLogger(Service.class);

    /**
     * Returns information on Patches in Development.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with Patch data.
     */
    @GET
    @Path("/patch/development/{type}")
    public Response getDevelopmentPatchData(@PathParam("type") String issueType) {

        JSONObject responseJSON = new JSONObject();
        try {
            LOGGER.info("Querying " + issueType + " patches in Development.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, GetTableDataServiceImpl.getInstance().getDevPatchData(issueType));
            LOGGER.info("Querying" + issueType + " patches in Development complete");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query Patches in Development.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .build();
    }

    /**
     * Returns information on Inactive Patches.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with Patch data.
     */
    @GET
    @Path("/patch/inactive/{type}")
    public Response getInactivePatchData(@PathParam("type") String issueType) {

        JSONObject responseJSON = new JSONObject();
        try {
            LOGGER.info("Querying " + issueType + " inactive patches.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, GetTableDataServiceImpl.getInstance().getInactivePatchData(issueType));
            LOGGER.info("Querying " + issueType + " inactive patches complete");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query Inactive Patches.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .build();
    }

    /**
     * Returns information on Patches in Signing.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with Patch data.
     */
    @GET
    @Path("/patch/signing/{type}")
    public Response getSigningPatchData(@PathParam("type") String issueType) {

        JSONObject responseJSON = new JSONObject();
        try {
            LOGGER.info("Querying " + issueType + " signing patches.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, GetTableDataServiceImpl.getInstance().getSigningPatchData(issueType));
            LOGGER.info("Querying" + issueType + " signing patches complete");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query Patches in Signing.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .build();
    }

    /**
     * Returns information on Released Patches.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with Patch data.
     */
    @GET
    @Path("/patch/released/{type}")
    public Response getReleasedPatchData(@PathParam("type") String issueType) {
        JSONObject responseJSON = new JSONObject();
        try {
            LOGGER.info("Querying " + issueType + " inactive patches.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, GetTableDataServiceImpl.getInstance().getReleasedPatchData(issueType));
            LOGGER.info("Querying " + issueType + " inactive patches complete");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query Released Patches.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .build();
    }

    /**
     * Returns information on all Patches.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with Patch data.
     */
    @GET
    @Path("/patch/summary/{type}")
    public Response getSummaryPatchData(@PathParam("type") String issueType) {
        JSONObject responseJSON = new JSONObject();
        try {
            LOGGER.info("Querying " + issueType + " patches.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, GetTableDataServiceImpl.getInstance().getSummaryPatchData(issueType));
            LOGGER.info("Querying complete");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query Patch summary.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .build();
    }

    /**
     * Returns the number of JIRA Issues.
     *
     * @param issueType type of JIRA issue (customer or proactive).
     * @return Response with number of JIRA issues.
     */
    @GET
    @Path("/patch/count/{type}")
    public Response getTotalJIRAIssues(@PathParam("type") String issueType) {

        JSONObject responseJSON = new JSONObject();
        String response;
        try {
            LOGGER.info("Querying number of " + issueType + " patches.");
            response = GetTableDataServiceImpl.getInstance().getJIRACount(issueType);
            LOGGER.info("Querying number of" + issueType + " patches complete.");
            responseJSON.put(RESPONSE_TYPE, SUCCESS);
            responseJSON.put(RESPONSE_MESSAGE, response);
        } catch (PatchAnalysisException e) {
            LOGGER.error("Failed to query number of Patches.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, e.getMessage());
        }
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .header("Access-Control-Allow-Credentials", true)
                .build();

    }

    /**
     * Sends emails on Proactive and Customer related JIRA issues.
     *
     * @return Response with confirmation of email being sent.
     */
    @POST
    @Path("/emails")
    public Response sendEmails() {

        JSONObject responseJSON = new JSONObject();
        //send mail on Proactive JIRA issues.
        try {
            LOGGER.info("Executing process to send email on Internal JIRA issues.");
            SendEmailsServiceImpl.getInstance().sendEmail(false);
        } catch (PatchAnalysisException e) {
            String message = "Both emails (proactive and internal JIRA issues) were not sent.";
            LOGGER.error(message, e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, message);
            return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Credentials", true)
                    .build();
        }
        //send mail on Customer JIRA issues.
        try {
            LOGGER.info("Executing process to send email on Customer related JIRA issues.");
            SendEmailsServiceImpl.getInstance().sendEmail(true);
        } catch (PatchAnalysisException e) {
            String message = "Email on proactive JIRA issues sent, but customer related email not sent.";
            LOGGER.error(message, e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, message);
            return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Credentials", true)
                    .build();
        }
        responseJSON.put(RESPONSE_TYPE, SUCCESS);
        responseJSON.put(RESPONSE_MESSAGE, "Emails sent successfully.");
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-Credentials", true)
                .build();
    }

    /**
     * Updates the DB containing all Patch and JIRA data.
     *
     * @return Response confirmation the update of the DB.
     */
    @POST
    @Path("/database")
    public Response updateDatabase() {

        JSONObject responseJSON = new JSONObject();
        String message = "Data could not be updated";
        //update DB with data on Proactive JIRA issues.
        try {
            LOGGER.info("Updating DB with data on Internal JIRA issues.");
            UpdateDatabaseServiceImpl.getInstance().updateDB(false);
        } catch (PatchAnalysisException e) {
            LOGGER.error(message + " for proactive issues.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, message);
            return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Credentials", true)
                    .build();
        }
        //update DB with data on Customer JIRA issues.
        try {
            LOGGER.info("Updating DB with data on Customer JIRA issues.");
            UpdateDatabaseServiceImpl.getInstance().updateDB(true);
        } catch (PatchAnalysisException e) {
            LOGGER.error(message + " for customer issues.", e);
            responseJSON.put(RESPONSE_TYPE, ERROR);
            responseJSON.put(RESPONSE_MESSAGE, message);
            return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-origin", '*')
                    .header("Access-Control-Allow-Credentials", true)
                    .build();
        }

        message = "Data was updated successfully.";
        LOGGER.info(message);
        responseJSON.put(RESPONSE_TYPE, SUCCESS);
        responseJSON.put(RESPONSE_MESSAGE, message);
        return Response.ok(responseJSON, MediaType.APPLICATION_JSON)
                .header("Access-Control-Allow-origin", '*')
                .header("Access-Control-Allow-Credentials", true)
                .build();
    }
}
