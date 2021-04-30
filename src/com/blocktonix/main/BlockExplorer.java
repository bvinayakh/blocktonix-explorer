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
import com.blocktonix.main.schedulers.SyncSchedulerContractEthValue;
import com.blocktonix.main.schedulers.SyncSchedulerLatestBlocks;

public class BlockExplorer
{

  public static final Logger logger = LoggerFactory.getLogger(BlockExplorer.class);

  public static void main(String[] args)
  {
    // do not delete
    // persist coins list from coingecko to db
    // use or uncomment only when needed
//    try
//    {
//      TokenDBOperations tokenOperations = new TokenDBOperations();
//      tokenOperations.storeTokenInfo(Utilities.getCoinsList());
//    }
//    catch (JsonProcessingException e)
//    {
//      e.printStackTrace();
//    }
    // do not delete

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

    // job to update eth value for contracts in db
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
