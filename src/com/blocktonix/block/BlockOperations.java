package com.blocktonix.block;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

public class BlockOperations
{
  public static final Logger logger = LoggerFactory.getLogger(BlockOperations.class);

  private Web3j web3 = null;

  public BlockOperations(Web3j web3j)
  {
    this.web3 = web3j;
  }

  public BigInteger getLatestBlockNumber() throws InterruptedException, ExecutionException, IOException
  {
    return web3.ethBlockNumber().send().getBlockNumber();
    // return web3.ethBlockNumber().sendAsync().get().getBlockNumber();
  }

  public EthBlock getLatestBlock() throws InterruptedException, ExecutionException, IOException
  {
    return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send();
    // return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).sendAsync().get();
  }

  public EthBlock getBlock(BigInteger blockNumber) throws InterruptedException, ExecutionException, IOException
  {
    return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send();
    // return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber),
    // true).sendAsync().get();
  }

  public List<BigInteger> getFirstFiveHundredBlocks() throws InterruptedException, ExecutionException, IOException
  {
    ArrayList<BigInteger> blockNumbersList = new ArrayList<BigInteger>();
    BigInteger latestBlockNumber = getLatestBlockNumber();
    blockNumbersList.add(latestBlockNumber);
    for (int counter = 1; counter < 500; counter++)
    {
      Integer counterInt = Integer.valueOf(counter);
      blockNumbersList.add(latestBlockNumber.subtract(BigInteger.valueOf(counterInt)));
    }
    return blockNumbersList;

  }

}
