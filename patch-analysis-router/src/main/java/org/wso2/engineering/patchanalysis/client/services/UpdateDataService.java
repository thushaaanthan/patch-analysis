package org.wso2.engineering.patchanalysis.client.services;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.engineering.patchanalysis.client.utils.Constants;
import org.wso2.engineering.patchanalysis.client.utils.PropertyReader;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to act as a router between the backend microservice and the frontend when updating the data.
 */
public class UpdateDataService extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RouterService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try (PrintWriter out = resp.getWriter()) {
            resp.setContentType("application/json");
            out.print(ServiceExecutor.executeGetService(req.getPathInfo()));
        } catch (JSONException e) {
            log.error("Failed to get the response from the backend service. " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        PropertyReader properties = new PropertyReader();
        String username = String.valueOf(req.getSession().getAttribute("user"));
        boolean authorized = false;

        for (String user : properties.getDatabaseUpdateAllowedUserRoles()) {
            if (user.trim().contains(username)) {
                authorized = true;
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            resp.setContentType("application/json");
            out.print(ServiceExecutor.executePostService(Constants.DATABASE_UPDATE_URL, req.getReader().readLine(),
                    authorized));
        } catch (JSONException | IOException e) {
            log.error("Failed to get the response from the backend service. " + e.getMessage(), e);
        }
    }

}
