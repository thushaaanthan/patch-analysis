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

package org.wso2.engineering.patchanalysis.client.filters;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.engineering.patchanalysis.client.utils.PropertyReader;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.wso2.engineering.patchanalysis.client.utils.Constants.EMAIL_ADDRESS;
import static org.wso2.engineering.patchanalysis.client.utils.Constants.JWT_ASSERTION;
import static org.wso2.engineering.patchanalysis.client.utils.Constants.ROLE;

/**
 * This class is for handling sso configuration.
 */
public class JWTActions implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JWTActions.class);
    private static final PropertyReader propertyReader = new PropertyReader();

    /**
     * This method is for get public key
     *
     * @return return for getting public key
     * @throws IOException              if unable to load the file
     * @throws KeyStoreException        if unable to get instance
     * @throws CertificateException     if unable to certify
     * @throws NoSuchAlgorithmException cause by other underlying exceptions(KeyStoreException)
     */
    private static PublicKey getPublicKey() throws IOException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException {

        InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyReader
                .getSSOKeyStoreName());
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(file, propertyReader.getSSOKeyStorePassword().toCharArray());
        Certificate cert = keystore.getCertificate(propertyReader.getSSOCertAlias());
        return cert.getPublicKey();
    }

    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ssoRedirectUrl = propertyReader.getSSORedirectUrl();

        String jwtString = request.getHeader(JWT_ASSERTION);

        if (jwtString == null || jwtString.equals("")) {
            if (log.isDebugEnabled()) {
                log.debug("Redirecting to {}");
            }
            response.sendRedirect(ssoRedirectUrl);
        } else {
            String username = null;
            String roles = null;

            try {

                SignedJWT signedJWT = SignedJWT.parse(jwtString);
                PublicKey publicKey = getPublicKey();
                if (publicKey != null) {
                    JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

                    if (signedJWT.verify(verifier)) {
                        if (log.isDebugEnabled()) {
                            log.debug("JWT validation success for token: {}", jwtString);
                        }
                        username = signedJWT.getJWTClaimsSet().getClaim(EMAIL_ADDRESS)
                                .toString();
                        roles = signedJWT.getJWTClaimsSet().getClaim(ROLE).toString();

                        if (roles != null) {
                            List<String> listOfRoles = Arrays.asList(roles.split(","));
                            if (!listOfRoles.contains(propertyReader.getAllowedUserRole())) {
                                log.error("User does not have a valid role permissions.");
                                response.sendError(403);
                                return;
                            }
                        }

                    } else {
                        log.error("JWT validation failed for token: {" + jwtString + "}");
                        response.sendRedirect(ssoRedirectUrl);
                        return;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Declining access to " + " since SSO Identity Provider public key does not exist");
                    }
                }

            } catch (ParseException | JOSEException | CertificateException | NoSuchAlgorithmException |
                    KeyStoreException e) {
                log.error("Declining access to " + request.getRequestURL() + " since JWT token " + jwtString +
                        " validation failed", e);
                response.sendError(401);

            }

            if (username != null && roles != null) {
                request.getSession().setAttribute("user", username);
                request.getSession().setAttribute("roles", roles);
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (ServletException e) {
                    log.error("Failed to pass the request, response objects through filters", e);
                }
            } else {
                response.sendRedirect(ssoRedirectUrl);
            }
        }
    }

    public void destroy() {
        // Do nothing
    }
}
