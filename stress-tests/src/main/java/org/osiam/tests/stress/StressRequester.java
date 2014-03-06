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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class StressRequester {

    public static void main(String[] args) {

        int number = 0;

        try {
            Scheduler scheduler;
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();

//            addRequesterJob(scheduler, 500, number++);
//            addRequesterJob(scheduler, 701, number++);
//            addRequesterJob(scheduler, 902, number++);
//            addRequesterJob(scheduler, 1103, number++);
//            addRequesterJob(scheduler, 1304, number++);
//
//            addRequesterJob(scheduler, 555, number++);
//            addRequesterJob(scheduler, 756, number++);
//            addRequesterJob(scheduler, 957, number++);
//            addRequesterJob(scheduler, 1158, number++);
//            addRequesterJob(scheduler, 1359, number++);
            
            addAggregatorJib(scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }

    }

    private static void addAggregatorJib(Scheduler scheduler) throws SchedulerException{
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime(); 
        
        JobDetail job = newJob(AggregatorJob.class)
                .withIdentity("a job", "a group")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("a trigger", " a group")
//                .startAt(startDate)
                .startNow()
                .withSchedule(simpleSchedule()
//                        .withIntervalInHours(12)
                        .withIntervalInSeconds(15)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }
    
    private static void addRequesterJob(Scheduler scheduler, long milliSeconds, int number) throws SchedulerException {

        JobDetail job = newJob(RequesterJob.class)
                .withIdentity("r job" + number, "r group" + number)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("r trigger" + number, "r group" + number)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(milliSeconds)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }

}
