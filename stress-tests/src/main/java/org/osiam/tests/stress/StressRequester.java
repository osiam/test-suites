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

        int number = 1;
        JobDetail job = getRequesterJob(number);
        Trigger trigger = getTrigger(1000, number);

        number++;
        JobDetail job2 = getRequesterJob(number);
        Trigger trigger2 = getTrigger(500, number);

        try {
            Scheduler scheduler;
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);
        } catch (SchedulerException e) {
            System.out.print(e.getMessage());
        }

    }
    
    private static JobDetail getRequesterJob(int number){
        JobDetail job = newJob(RequesterJob.class)
                .withIdentity("job" + number, "group" + number)
                .build();
        
        return job;
    }
    
    private static Trigger getTrigger(long milliSeconds, int number){
        Trigger trigger = newTrigger()
                .withIdentity("trigger" + number, "group" + number)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(milliSeconds)
                        .repeatForever())
                .build();
        
        return trigger;
    }

}
