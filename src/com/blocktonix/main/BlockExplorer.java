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
import com.blocktonix.main.schedulers.SyncSchedulerCoinsGecko;
import com.blocktonix.main.schedulers.SyncSchedulerContractEthValue;
import com.blocktonix.main.schedulers.SyncSchedulerLatestBlocks;
import com.blocktonix.utils.ApplicationProperties;

public class BlockExplorer
{

  public static final Logger logger = LoggerFactory.getLogger(BlockExplorer.class);

  public static void main(String[] args)
  {
    // persist coins list from coingecko to db
    // try
    // {
    // TokenDBOperations tokenOperations = new TokenDBOperations();
    // tokenOperations.storeTokenInfo(Utilities.getCoinsList());
    // }
    // catch (JsonProcessingException e)
    // {
    // e.printStackTrace();
    // }

    // persist coins list from coingecko to db
    if (Boolean.valueOf(new ApplicationProperties().getProperty("sync.coinsgecko")))
    {
      JobDetail job = JobBuilder.newJob(SyncSchedulerCoinsGecko.class).build();
      Trigger t = TriggerBuilder.newTrigger().withIdentity("synccoinsgecko")
          .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(6).repeatForever()).build();
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

    // trigger a blockchain sync job to run every 1 minute to sync 10 latest blocks
    if (Boolean.valueOf(new ApplicationProperties().getProperty("sync.blocks")))
    {
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

    // job to update eth value for contracts in db
    if (Boolean.valueOf(new ApplicationProperties().getProperty("sync.contracts.ethvalue")))
    {
      JobDetail updateContractEthValueJob = JobBuilder.newJob(SyncSchedulerContractEthValue.class).build();
      Trigger updateContractEthValueTrigger = TriggerBuilder.newTrigger().withIdentity("syncontractethvalue")
          .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever()).build();
      try
      {
        Scheduler s = StdSchedulerFactory.getDefaultScheduler();
        s.start();
        s.scheduleJob(updateContractEthValueJob, updateContractEthValueTrigger);
      }
      catch (NullPointerException e)
      {
        logger.error("Null Pointer Exception " + e.getMessage());
      }
      catch (SchedulerException e)
      {
        logger.error("Error updating contract eth values " + e.getMessage());
      }
    }

  }
}
