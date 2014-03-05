package org.osiam.tests.stress;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RequesterJob implements Job {

    private OsiamContext osiamValues;

    public RequesterJob() {
        osiamValues = OsiamContext.getInstance();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println(osiamValues.getValidAccessToken() == null);
            int i = (int) (Math.random() * 30 + 1);

            if (i > 0 && i <= 10) {
                createNewUser();
            }
            else if (i > 10 && i <= 18) {
                searchUser();
            } else if (i > 18 && i <= 26) {
                getUser();
            } else if (i > 26 && i <= 28) {
                updateUser();
            } else if (i > 28 && i <= 29) {
                replaceUser();
            } else {
                deleteUser();
            }

        } catch (Throwable e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createNewUser() {
        System.out.println("Creating a new User");
    }

    private void updateUser() {
        System.out.println("Updating a User");
    }

    private void replaceUser() {
        System.out.println("Replace a User");
    }

    private void deleteUser() {
        System.out.println("deleting a User");
    }

    private void searchUser() {
        System.out.println("searching for Users");
    }

    private void getUser() {
        System.out.println("get a Users");
    }

}
