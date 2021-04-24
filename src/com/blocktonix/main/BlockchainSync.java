package com.blocktonix.main;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockchainSync
{

  public static final Logger logger = LoggerFactory.getLogger(BlockchainSync.class);

  public static void main(String[] args)
  {
    // trigger a blockchain sync job to run every 1 minute to sync 10 latest blocks
    JobDetail job = JobBuilder.newJob(SyncSchedulerLatestBlocks.class).build();
    Trigger t = TriggerBuilder.newTrigger().withIdentity("syncblocks")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever()).build();
    try
    {
      Scheduler s = StdSchedulerFactory.getDefaultScheduler();
      s.start();
      s.scheduleJob(job, t);
    }
    catch (SchedulerException e)
    {
      e.printStackTrace();
    }

  }
}
