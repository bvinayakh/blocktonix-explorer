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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.BlockTask;
import com.blocktonix.block.dao.BlockDBOperations;
import com.blocktonix.utils.Constants;

public class ForwardSync implements Runnable
{
  public static final Logger logger = LoggerFactory.getLogger(ForwardSync.class);

  private List<BigInteger> forwardBlockNumbersList = null;

  public ForwardSync(List<BigInteger> blocksList)
  {
    this.forwardBlockNumbersList = blocksList;
  }

  @Override
  public void run()
  {
    for (BigInteger blockNumber : forwardBlockNumbersList)
    {
      logger.info("Fetching forward block : " + blockNumber);
      processBlock(blockNumber);
      try
      {
        Thread.sleep(3000);
      }
      catch (InterruptedException e)
      {
        System.err.println("Error while waiting for new block creation " + e.getMessage());
      }
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
