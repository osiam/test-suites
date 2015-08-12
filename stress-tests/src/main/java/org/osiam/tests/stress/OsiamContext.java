/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.tests.stress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

public class OsiamContext {
    private static OsiamContext contextSingelton = null;
    private static String AUTH_ENDPOINT_ADDRESS;
    private static String RESOURCE_ENDPOINT_ADDRESS;
    private static final String CLIENT_ID = "example-client";
    private static final String CLIENT_SECRET = "secret";

    private AccessToken accessToken = null;
    private HashMap<String, OsiamConnector> connectors = new HashMap<String, OsiamConnector>();

    private List<User> users = new ArrayList<User>();

    private OsiamContext() {
    }

    public static OsiamContext getInstance() {
        if (contextSingelton == null) {
            contextSingelton = new OsiamContext();
        }
        return contextSingelton;
    }

    public OsiamConnector getConnector(String key) {
        OsiamConnector osiamConnector = connectors.get(key);
        if (osiamConnector == null) {
            OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                    setAuthServerEndpoint(AUTH_ENDPOINT_ADDRESS).
                    setResourceServerEndpoint(RESOURCE_ENDPOINT_ADDRESS).
                    setClientId(CLIENT_ID).
                    setClientSecret(CLIENT_SECRET);
            osiamConnector = oConBuilder.build();
            connectors.put(key, osiamConnector);
        }
        return osiamConnector;
    }

    public AccessToken getValidAccessToken() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getConnector("accessToken").retrieveAccessToken("admin", "koala", Scope.ADMIN);
        }
        return accessToken;
    }

    public synchronized void setListOfUsers(List<User> users) {
        this.users = users;
    }

    public void setResourcesEndpoint(String resourceEndpoint) {
        AUTH_ENDPOINT_ADDRESS = resourceEndpoint + "/osiam-auth-server";
        RESOURCE_ENDPOINT_ADDRESS = resourceEndpoint + "/osiam-resource-server";
    }

    public synchronized String retrieveSingleUserId() {
        String userId = null;
        if (users.size() > 0) {
            int randomPosition = (int) (Math.random() * users.size());
            User user = users.get(randomPosition);
            if (!user.getUserName().equals("admin")) {
                userId = user.getId();
                users.remove(randomPosition);
            }
        }
        return userId;
    }

    public String getResourceEndpointAddress() {
        return RESOURCE_ENDPOINT_ADDRESS;
    }
}
