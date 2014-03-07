package org.osiam.tests.stress;

import static org.apache.http.HttpStatus.SC_OK;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.client.oauth.AccessToken;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregatorJob implements Job {

    private String jobName;
    private AccessToken accessToken;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobName = context.getJobDetail().getKey().getName();
        accessToken = OsiamContext.getInstance().getValidAccessToken();

        try {
            System.out.println("Retrieving metrics data");
            
            DefaultHttpClient httpclient = new DefaultHttpClient();
            URI uri = new URI(OsiamContext.getInstance().getResourceEndpointAddess() +"/Metrics");

            HttpGet realWebResource = new HttpGet(uri);
            realWebResource.addHeader("Authorization", "Bearer " + accessToken.getToken());
            realWebResource.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());

            HttpResponse response = httpclient.execute(realWebResource);

            int httpStatus = response.getStatusLine().getStatusCode();

            InputStream content = response.getEntity().getContent();
            if (httpStatus != SC_OK) {
                String inputStreamStringValue = IOUtils.toString(content, "UTF-8");
                logError(inputStreamStringValue);
            }
            
            int mb = 1024*1024;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content);
            String memoryUsage = "" + (rootNode.get("gauges").get("jvm.memory.total.used").get("value").asInt() / mb);
            
            DataStorage.storeData(memoryUsage);
        } catch (Throwable e) {
            logError(e);
        }
    }
    
    private void logError(Throwable error){
        logError(error.getMessage());
        error.printStackTrace();
    }
    
    private void logError(String errorMessage){
        System.out.println(jobName + ": " + errorMessage);
        
        try{
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("aggregatorError.txt", true)));
        
        out.println(new Date() + ": " + errorMessage);
        out.close();
        }catch(IOException e){
        }
    }

}
