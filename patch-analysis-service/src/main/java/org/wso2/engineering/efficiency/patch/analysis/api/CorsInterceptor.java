package org.wso2.engineering.efficiency.patch.analysis.api;


import org.apache.commons.lang.StringUtils;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.interceptor.RequestInterceptor;

/**
 * Applying header for allowing cross origin requests.
 */
public class CorsInterceptor implements RequestInterceptor {

    @Override
    public boolean interceptRequest(Request request, Response response) {

        response.setHeader("Access-Control-Allow-Origin", "*");

        if (StringUtils.isNotBlank(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        }
        return true;
    }
}
