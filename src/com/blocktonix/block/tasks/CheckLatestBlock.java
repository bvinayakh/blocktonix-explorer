package com.blocktonix.block.tasks;

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
import com.blocktonix.block.BlockTask;
import com.blocktonix.utils.Constants;

public class CheckLatestBlock implements Job
{
  public static final Logger logger = LoggerFactory.getLogger(CheckLatestBlock.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException
  {

    try
    {
      processBlock(new BlockOperations().getLatestBlockNumber());
    }
    catch (ConstraintViolationException e)
    {
      logger.error("Block already exists in DB " + e.getMessage());
    }
    catch (PersistenceException e)
    {
      logger.error("Block already exists in DB " + e.getMessage());
    }
    catch (IOException e)
    {
      logger.error("Error getting latest block number " + e.getMessage());
    }
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
