/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregatorJob implements Job {

    final Logger logger = LoggerFactory.getLogger(AggregatorJob.class);
    
    private String jobName;
    private AccessToken accessToken;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobName = context.getJobDetail().getKey().getName();
        accessToken = OsiamContext.getInstance().getValidAccessToken();

        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            String dateOut = dateFormatter.format(new Date());
            System.out.println(dateOut + "; Retrieving metrics data");

            DefaultHttpClient httpclient = new DefaultHttpClient();
            URI uri = new URI(OsiamContext.getInstance().getResourceEndpointAddress() + "/Metrics");

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

            int mb = 1024 * 1024;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content);
            String memoryUsage = "" + (rootNode.get("gauges").get("jvm.memory.total.used").get("value").asInt() / mb);

            DataStorage.storeData(memoryUsage);
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
