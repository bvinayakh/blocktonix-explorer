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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.BlockTask;

public class BlockchainSync
{

  public static final Logger logger = LoggerFactory.getLogger(BlockchainSync.class);

  private List<String> responsesList = null;
  private Web3j web3 = null;

  public void sync()
  {
    responsesList = new ArrayList<String>();
    // String infuraEndpoint = "https://mainnet.infura.io/v3/3deb0847ce9942568005689574ba69db";
    String infuraEndpoint = "http://135.181.213.174:8545";
    web3 = Web3j.build(new HttpService(infuraEndpoint));
    BlockOperations blockOps = new BlockOperations(web3);
    try
    {
      List<BigInteger> blockNumbersList = blockOps.getFirstFiveHundredBlocks();
      // blockNumbersList.parallelStream().forEach(blockNumber -> processBlock(blockNumber));
      for (BigInteger blockNumber : blockNumbersList)
      {
        processBlock(blockNumber);
      }
    }
    catch (InterruptedException | ExecutionException | IOException e)
    {
      e.printStackTrace();
    }
    System.out.println(responsesList);
  }


  public static void main(String[] args)
  {
    new BlockchainSync().sync();

  }


  private void processBlock(BigInteger blockNumber)
  {
    ExecutorService executor = Executors.newCachedThreadPool();
    Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
    callables.add(new BlockTask(web3, blockNumber));
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
      e.printStackTrace();
    }
    executor.shutdown();
  }


}
