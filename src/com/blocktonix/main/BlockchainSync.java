package com.blocktonix.main;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.BlockTask;
import com.blocktonix.block.tasks.CheckLatestBlock;
import com.blocktonix.utils.ApplicationProperties;
import com.blocktonix.utils.Constants;

public class BlockchainSync
{

  public static final Logger logger = LoggerFactory.getLogger(BlockchainSync.class);

  private List<String> responsesList = null;

  public void sync()
  {
    responsesList = new ArrayList<String>();
    BlockOperations blockOps = new BlockOperations();
    try
    {
      // forward sync
      processLatestBlock();
      // historical sync
      List<BigInteger> blockNumbersList = blockOps.getFirstFiveHundredBlocks();
      String runningMode = ApplicationProperties.getProperties("scan.mode");
      if (runningMode.equalsIgnoreCase("single"))
      {
        for (BigInteger blockNumber : blockNumbersList)
        {
          processBlock(blockNumber);
        }
      }
      else
        blockNumbersList.parallelStream().forEach(blockNumber -> processBlock(blockNumber));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void processLatestBlock()
  {
    JobDetail job = JobBuilder.newJob(CheckLatestBlock.class).build();
    Trigger t = TriggerBuilder.newTrigger().withIdentity("checklatestblock")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();
    try
    {
      Scheduler s = StdSchedulerFactory.getDefaultScheduler();
      s.start();
      s.scheduleJob(job, t);
    }
    catch (SchedulerException e)
    {
      logger.error("Error processing latest block " + e.getMessage());
    }

  }


  public static void main(String[] args)
  {
    new BlockchainSync().sync();

  }


  private void processBlock(BigInteger blockNumber)
  {
    ExecutorService executor = Executors.newFixedThreadPool(20);
    Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
    callables.add(new BlockTask(Constants.web3, blockNumber));
    List<Future<String>> futures;
    try
    {
      futures = executor.invokeAll(callables);
      for (Future<String> future : futures)
      {
        String responseContent = future.get();
        responsesList.add(responseContent);
      }
      futures.clear();
    }
    catch (InterruptedException | ExecutionException e)

    {
      logger.error("Execution Error while processing block " + e.getMessage());
    }
    executor.shutdown();
  }


}
