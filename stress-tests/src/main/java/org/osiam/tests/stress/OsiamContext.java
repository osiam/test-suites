package org.osiam.tests.stress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.User;

public class OsiamContext {
    private static OsiamContext contextSingelton = null;
    private static final String AUTH_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-auth-server";
    private static final String RESOURCE_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-resource-server";
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
        if(osiamConnector == null){
        OsiamConnector.Builder oConBuilder = new OsiamConnector.Builder().
                setAuthServiceEndpoint(AUTH_ENDPOINT_ADDRESS).
                setResourceEndpoint(RESOURCE_ENDPOINT_ADDRESS).
                setClientId(CLIENT_ID).
                setClientSecret(CLIENT_SECRET).
                setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS).
                setUserName("marissa").
                setPassword("koala").
                setScope(Scope.ALL);
        osiamConnector = oConBuilder.build();
        connectors.put(key, osiamConnector);
        }
        return osiamConnector;
    }

    public AccessToken getValidAccessToken() {
        if (accessToken == null)
        {
            accessToken = getConnector("accessToken").retrieveAccessToken();
        }
        if (accessToken.isExpired()) {
            getConnector("accessToken").refreshAccessToken(accessToken, Scope.ALL);
        }
        return accessToken;
    }

    public void setListOfUsers(List<User> users) {
        this.users = users;
    }

    public String retrieveSingleUserId() {
        String userId = null;
        if (users.size() > 0) {
            int randomPosition = (int) (Math.random() * users.size());
            User user = users.get(randomPosition);
            userId = user.getId();
            users.remove(randomPosition);
        }
        return userId;
    }
}
