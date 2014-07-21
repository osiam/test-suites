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

import static org.apache.http.HttpStatus.SC_OK;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.http.entity.ContentType;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregatorJob implements Job {

    final Logger logger = LoggerFactory.getLogger(AggregatorJob.class);

    private String jobName;
    private OsiamConnector osiamConnector;
    private AccessToken accessToken;
    private static final String BEARER = "Bearer ";
    private static final int CONNECT_TIMEOUT = 2500;
    private static final int READ_TIMEOUT = 5000;
    private static final Client client = ClientBuilder.newClient(new ClientConfig()
            .connectorProvider(new ApacheConnectorProvider())
            .property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED)
            .property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT)
            .property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT)
            .property(ApacheClientProperties.CONNECTION_MANAGER, new PoolingHttpClientConnectionManager()));

    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobName = context.getJobDetail().getKey().getName();
        osiamConnector = OsiamContext.getInstance().getConnector(jobName);
        accessToken = OsiamContext.getInstance().getValidAccessToken();

        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            String dateOut = dateFormatter.format(new Date());
            System.out.println(dateOut + "; Retrieving metrics data");

            WebTarget targetEndpoint = client.target(OsiamContext.getInstance().getResourceEndpointAddress());

            Response response = targetEndpoint.path("/Metrics")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", BEARER + accessToken.getToken())
                    .header("Accept", ContentType.APPLICATION_JSON.getMimeType())
                    .get();

            StatusType status = response.getStatusInfo();
            String content = response.readEntity(String.class);
            if (status.getStatusCode() != SC_OK) {
                logError(content);
            }

            int mb = 1024 * 1024;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content);
            String memoryUsage = "" + (rootNode.get("gauges").get("jvm.memory.total.used").get("value").asInt() / mb);
            String totalUser = getTotalUsers();

            DataStorage.storeData(memoryUsage, totalUser);
        } catch (Throwable e) {
            logError(e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error(jobName + ": " + sw.toString());
        }
    }

    private void logError(Throwable error) {
        logError(error.getMessage());
        error.printStackTrace();
    }

    private String getTotalUsers() {
        Query query = new QueryBuilder().count(1).build();

        SCIMSearchResult<User> searchResult = osiamConnector.searchUsers(query, accessToken);

        return (new Long(searchResult.getTotalResults())).toString();
    }

    private void logError(String errorMessage) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        String dateOut = dateFormatter.format(new Date());

        System.out.println(dateOut + ": " + jobName + ": " + errorMessage);

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("aggregatorError.txt", true)));

            out.println(dateOut + ": " + errorMessage);
            out.close();
        } catch (IOException e) {
        }
    }

}
