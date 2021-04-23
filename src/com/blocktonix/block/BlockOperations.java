package com.blocktonix.block;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import com.blocktonix.utils.Constants;

public class BlockOperations
{
  public static final Logger logger = LoggerFactory.getLogger(BlockOperations.class);

  public BlockOperations()
  {}

  public BigInteger getLatestBlockNumber() throws IOException
  {
    return Constants.web3.ethBlockNumber().send().getBlockNumber();
    // return web3.ethBlockNumber().sendAsync().get().getBlockNumber();
  }

  public EthBlock getLatestBlock() throws IOException
  {
    return Constants.web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send();
    // return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).sendAsync().get();
  }

  public EthBlock getBlock(BigInteger blockNumber) throws IOException
  {
    return Constants.web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send();
    // return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber),
    // true).sendAsync().get();
  }

  public List<BigInteger> getFirstThousandBlocks() throws IOException
  {
    ArrayList<BigInteger> blockNumbersList = new ArrayList<BigInteger>();
    // always process 1 block less than the current block
    BigInteger latestBlockNumber = getLatestBlockNumber().subtract(BigInteger.valueOf(1));
    blockNumbersList.add(latestBlockNumber);
    for (int counter = 1; counter < 1000; counter++)
    {
      Integer counterInt = Integer.valueOf(counter);
      blockNumbersList.add(latestBlockNumber.subtract(BigInteger.valueOf(counterInt)));
    }
    return blockNumbersList;
  }
  
  public List<BigInteger> getFirstTenBlocks() throws IOException
  {
    ArrayList<BigInteger> blockNumbersList = new ArrayList<BigInteger>();
    // always process 1 block less than the current block
    BigInteger latestBlockNumber = getLatestBlockNumber();
    blockNumbersList.add(latestBlockNumber);
    for (int counter = 1; counter < 10; counter++)
    {
      Integer counterInt = Integer.valueOf(counter);
      blockNumbersList.add(latestBlockNumber.subtract(BigInteger.valueOf(counterInt)));
    }
    return blockNumbersList;
  }

  public List<BigInteger> getForwardBlocks() throws IOException
  {
    ArrayList<BigInteger> blockNumbersList = new ArrayList<BigInteger>();
    BigInteger latestBlockNumber = getLatestBlockNumber();
    blockNumbersList.add(latestBlockNumber);
    for (int counter = 1; counter < 100000; counter++)
    {
      Integer counterInt = Integer.valueOf(counter);
      blockNumbersList.add(latestBlockNumber.add(BigInteger.valueOf(counterInt)));
    }
    return blockNumbersList;

  }

}
