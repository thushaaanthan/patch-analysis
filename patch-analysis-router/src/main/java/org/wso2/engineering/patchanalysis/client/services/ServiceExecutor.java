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
package org.wso2.engineering.patchanalysis.client.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.engineering.patchanalysis.client.exceptions.RouterException;
import org.wso2.engineering.patchanalysis.client.utils.PropertyReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Manage backend calls.
 */
class ServiceExecutor {

    private static final Logger log = LoggerFactory.getLogger(ServiceExecutor.class);

    /**
     * Call the backend service for the GET requests.
     *
     * @param endpoint endpoint of the service
     * @return response from the backend/error object
     * @throws JSONException if creating a json from the response entity fails.
     */
    static JSONObject executeGetService(String endpoint) throws JSONException {

        PropertyReader properties = new PropertyReader();
        String url = properties.getMicroServiceUrl() + endpoint;
        JSONObject result = null;

        try (CloseableHttpClient httpClient = createTrustedHttpClient()) {
            // Create the request.
            URIBuilder builder = new URIBuilder(url);
            //builder.setParameter("username", username);
            HttpGet request = new HttpGet(builder.build());

            // Calling the micro service.
            HttpResponse response = httpClient.execute(request);

            // Build json response
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // parsing JSON
                result = new JSONObject(EntityUtils.toString(entity));
            }
        } catch (URISyntaxException | IOException | JSONException e) {
            result = new JSONObject();
            result.put("responseType", "Error");
            String message = "Failed to get response from server.";
            result.put("responseMessage", message);
            log.error(message + e.getMessage(), e);
        } catch (RouterException e) {
            result = new JSONObject();
            result.put("responseType", "Error");
            result.put("responseMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Create a trusted http client to initiate a secure connection with micro services.
     *
     * @return closeableHttpClient
     * @throws RouterException if the connection initiation fails
     */
    private static CloseableHttpClient createTrustedHttpClient() throws RouterException {

        PropertyReader properties = new PropertyReader();

        // Setting up authentication.
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(properties.getMicroServiceUsername(),
                properties.getMicroServicePassword());
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // Get the keystore file.
        InputStream file = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(properties.getTrustStoreServiceName());
        try {
            // Make the trusted connection.
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(file, properties.getTrustStoreServicePassword().toCharArray());
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, null).build();
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
            httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
            if (log.isDebugEnabled()) {
                log.debug("A secure connection is established with the micro service. ");
            }
            return httpClientBuilder.setDefaultCredentialsProvider(provider).build();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException |
                KeyManagementException e) {
            throw new RouterException("Failed to initiate the connection. ", e);
        }
    }

    static JSONObject executePostService(String endpoint, String payload, boolean authorized) throws JSONException {

        JSONObject result = null;
        if (authorized) {
            PropertyReader properties = new PropertyReader();
            String url = properties.getMicroServiceUrl() + endpoint;

            try (CloseableHttpClient httpClient = createTrustedHttpClient()) {
                // Create the request.
                URIBuilder builder = new URIBuilder(url);
                HttpPost request = new HttpPost(builder.build());
                request.setHeader(HttpHeaders.ACCEPT, "application/json");
                ObjectMapper mapper = new ObjectMapper();
                String requestBodyInString = mapper.writeValueAsString(payload);
                StringEntity requestBody = new StringEntity(requestBodyInString, "UTF-8");
                requestBody.setContentType("application/json");
                request.setEntity(requestBody);

                // Calling the micro service
                HttpResponse response = httpClient.execute(request);

                // Build json response
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // parsing JSON
                    result = new JSONObject(EntityUtils.toString(entity));
                }
            } catch (URISyntaxException | IOException | JSONException e) {
                result = new JSONObject();
                result.put("responseType", "Error");
                result.put("responseMessage", "Failed to get response from server");
                log.error("Failed to get response from server. " + e.getMessage(), e);
            } catch (RouterException e) {
                result = new JSONObject();
                result.put("responseType", "Error");
                result.put("responseMessage", e.getMessage());
                log.error(e.getMessage(), e);
            }
        } else {
            result = new JSONObject();
            result.put("responseType", "Error");
            result.put("responseMessage", "You are not authorized.");
            log.info("user is unauthorized to access the endpoint");
        }
        return result;
    }
}
