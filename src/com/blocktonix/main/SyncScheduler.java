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
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.BlockTaskCallable;
import com.blocktonix.utils.ApplicationProperties;
import com.blocktonix.utils.Constants;

public class SyncScheduler implements Job
{

  public static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException
  {
    BlockOperations blockOps = new BlockOperations();
    try
    {
      // forward sync
      // new Thread(new ForwardSync(blockOps.getForwardBlocks())).start();
      // historical sync
      List<BigInteger> blockNumbersList = blockOps.getFirstTenBlocks();
      System.out.println("Processing blocks " + blockNumbersList);
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

  private void processBlock(BigInteger blockNumber)
  {
    ExecutorService executor = Executors.newFixedThreadPool(50);
    Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
    callables.add(new BlockTaskCallable(Constants.web3, blockNumber));
    List<Future<String>> futures;
    try
    {
      futures = executor.invokeAll(callables);
      for (Future<String> future : futures)
      {
        future.get();
      }
      futures.clear();
    }
    catch (InterruptedException | ExecutionException e)
    {
      logger.error("Execution Error while processing block " + blockNumber + " " + e.getMessage());
    }
    catch (ConstraintViolationException e)
    {
      logger.error("Error while trying to persist block " + blockNumber + " " + e.getMessage());
    }
    executor.shutdown();
  }

}
