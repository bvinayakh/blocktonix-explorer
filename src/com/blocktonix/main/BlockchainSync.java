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
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.BlockTaskCallable;
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
      new Thread(new ForwardSync(blockOps.getForwardBlocks())).start();
      // historical sync
      List<BigInteger> blockNumbersList = blockOps.getFirstThousandBlocks();
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

  public static void main(String[] args)
  {
    new BlockchainSync().sync();

  }


  private void processBlock(BigInteger blockNumber)
  {
    ExecutorService executor = Executors.newFixedThreadPool(20);
    Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
    callables.add(new BlockTaskCallable(Constants.web3, blockNumber));
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
      logger.error("Execution Error while processing block " + blockNumber + e.getMessage());
    }
    executor.shutdown();
  }


}
