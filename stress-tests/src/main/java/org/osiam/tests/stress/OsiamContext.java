package org.osiam.tests.stress;

import java.util.ArrayList;
import java.util.List;

import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

public class OsiamContext {
    private static OsiamContext mySingelton = null;
    private static final String AUTH_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-auth-server";
    private static final String RESOURCE_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-resource-server";
    private static final String CLIENT_ID = "example-client";
    private static final String CLIENT_SECRET = "secret";

    private AccessToken accessToken = null;
    private List<User> users = new ArrayList<User>();

    private OsiamContext() {
    }

    public static OsiamContext getInstance() {
        if (mySingelton == null) {
            mySingelton = new OsiamContext();
        }
        return mySingelton;
    }

    public OsiamConnector getConnector() {
        OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                setAuthServiceEndpoint(AUTH_ENDPOINT_ADDRESS).
                setResourceEndpoint(RESOURCE_ENDPOINT_ADDRESS).
                setClientId(CLIENT_ID).
                setClientSecret(CLIENT_SECRET).
                setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS).
                setUserName("marissa").
                setPassword("koala").
                setScope(Scope.ALL);
        OsiamConnector oConnector = oConBuilder.build();
        return oConnector;
    }

    public AccessToken getValidAccessToken() {
        if (accessToken == null)
        {
            accessToken = getConnector().retrieveAccessToken();
        }
        if (accessToken.isExpired()) {
            getConnector().refreshAccessToken(accessToken, Scope.ALL);
        }
        return accessToken;
    }

    public void setListOfUsers(List<User> users) {
        this.users = users;
    }

    public String retrieveSingleUserId() {
        String userId = null;
        if (users.size() > 0) {
            int randomPosition = (int) (Math.random() * users.size() + 1);
            User user = users.get(randomPosition);
            userId = user.getId();
            users.remove(randomPosition);
        }
        return userId;
    }
}
