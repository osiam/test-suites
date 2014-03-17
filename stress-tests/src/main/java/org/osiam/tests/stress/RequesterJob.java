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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class RequesterJob implements Job {

    final Logger logger = LoggerFactory.getLogger(RequesterJob.class);
    
    private String jobName;
    private OsiamConnector osiamConnector;
    private AccessToken accessToken;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            jobName = context.getJobDetail().getKey().getName();
            osiamConnector = OsiamContext.getInstance().getConnector(jobName);
            accessToken = OsiamContext.getInstance().getValidAccessToken();

            int i = (int) (Math.random() * 30 + 1);

            if (i > 0 && i <= 10) {
                createNewUser();
            } else if (i > 10 && i <= 18) {
                searchUser();
            } else if (i > 18 && i <= 26) {
                getUser();
            } else if (i > 26 && i <= 28) {
                updateUser();
            } else if (i > 28 && i <= 29) {
                replaceUser();
            } else if (i == 30) {
                deleteUser();
            }

        } catch (ConflictException | NoResultException | ForbiddenException ex) {
            logError(ex);
        } catch (Throwable e) {
            logError(e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.error(jobName + ": " + sw.toString());
        }
    }

    private void createNewUser() {
        logMessage("Creating a new User");

        User user = RandomUser.getNewUser();
        osiamConnector.createUser(user, accessToken);
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 99 + 1);
    }

    private void updateUser() {
        logMessage("Updating a User");
        String userId = OsiamContext.getInstance().retrieveSingleUserId();
        logMessage("try to update user with the id " + userId);
        if (!Strings.isNullOrEmpty(userId)) {
            UpdateUser updateUser = new UpdateUser.Builder().updateExternalId(UUID.randomUUID().toString()).build();
            osiamConnector.updateUser(userId, updateUser, accessToken);
        }
    }

    private void replaceUser() {
        logMessage("Replace a User");
        String userId = OsiamContext.getInstance().retrieveSingleUserId();
        logMessage("try to replace user with the id " + userId);
        if (!Strings.isNullOrEmpty(userId)) {
            User replaceUser = RandomUser.getNewUser();
            osiamConnector.replaceUser(userId, replaceUser, accessToken);
        }
    }

    private void deleteUser() {
        logMessage("deleting a User");
        String userId = OsiamContext.getInstance().retrieveSingleUserId();
        logMessage("try to delete user with the id " + userId);
        if (!Strings.isNullOrEmpty(userId)) {
            osiamConnector.deleteUser(userId, accessToken);
        }
    }

    private void searchUser() throws UnsupportedEncodingException {
        logMessage("searching for Users");
        int randomNumber = getRandomNumber();
        String query = getCompletUserQueryString(randomNumber);
        SCIMSearchResult<User> queryResult = osiamConnector.searchUsers("filter=" + query,
                accessToken);
        if (queryResult.getTotalResults() == 0) {
            queryResult = osiamConnector.searchUsers("filter=",
                    accessToken);
        }
        OsiamContext.getInstance().setListOfUsers(queryResult.getResources());
    }

    private String getCompletUserQueryString(int randomNumber) throws UnsupportedEncodingException {
        String encoded = null;
        encoded = URLEncoder.encode("active eq \"true\""
                + " and addresses.postalCode co \"" + randomNumber + "\""
                + " and NOT(username eq \"marissa\")"
                + " and addresses.primary eq \"true\"", "UTF-8");
        return encoded;
    }

    private void getUser() {
        logMessage("get a Users");
        String userId = OsiamContext.getInstance().retrieveSingleUserId();
        logMessage("try to getuser with id: " + userId);
        if (!Strings.isNullOrEmpty(userId)) {
            osiamConnector.getUser(userId, accessToken);
        }
    }

    private void logMessage(String message) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        String dateOut = dateFormatter.format(new Date());
        
        System.out.println(dateOut + ": " + jobName + ": " + message);
    }
    
    private void logError(Throwable e) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        String dateOut = dateFormatter.format(new Date());
        
        System.out.println("Error!! " + dateOut + ": " + jobName + ": " + e.getMessage());
        e.printStackTrace();
    }

}
