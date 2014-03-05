package org.osiam.tests.stress;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class StressRequester {

    protected static final String AUTH_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-auth-server";
    protected static final String RESOURCE_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-resource-server";
    protected static final String CLIENT_ID = "example-client";
    protected static final String CLIENT_SECRET = "secret";

    public static void main(String[] args) {

        int number = 0;

        try {
            Scheduler scheduler;
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            
            addJob(scheduler, 500, number++);
            addJob(scheduler, 701, number++);
            addJob(scheduler, 902, number++);
            addJob(scheduler, 1103, number++);
            addJob(scheduler, 1304, number++);

            addJob(scheduler, 555, number++);
            addJob(scheduler, 756, number++);
            addJob(scheduler, 957, number++);
            addJob(scheduler, 1158, number++);
            addJob(scheduler, 1359, number++);
        } catch (SchedulerException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }

    }
    
    private static void addJob(Scheduler scheduler, long milliSeconds, int number) throws SchedulerException{
        
        JobDetail job =  newJob(RequesterJob.class)
                .withIdentity("job" + number, "group" + number)
                .build();
        
        Trigger trigger = newTrigger()
                .withIdentity("trigger" + number, "group" + number)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(milliSeconds)
                        .repeatForever())
                .build();
        
        scheduler.scheduleJob(job, trigger);
    }

}
