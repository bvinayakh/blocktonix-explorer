package com.blocktonix.block;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import com.blocktonix.block.dao.BlockDBOperations;

public class BlockTaskCallable implements Callable<String>
{
  public static final Logger logger = LoggerFactory.getLogger(BlockTaskCallable.class);

  private BigInteger blockNumber;

  private BlockOperations blockOps = null;
  private BlockDBOperations blockDbOperations = null;

  public BlockTaskCallable(Web3j web3, BigInteger blockNumber)
  {
    this.blockNumber = blockNumber;
    blockOps = new BlockOperations();
    blockDbOperations = new BlockDBOperations();
    System.out.println("processing historical block number :" + this.blockNumber);
  }

  @Override
  public String call() throws Exception
  {
    // String latestBlockNumberInDb = blockDbOperations.getBlocksInDb();
    String output = null;
    // if (!String.valueOf(block.getNumber()).equalsIgnoreCase(latestBlockNumberInDb))
    if (!blockDbOperations.getBlocksInDb().contains(blockNumber))
    {
      // store block and related transactions in block
      Block block = blockOps.getBlock(blockNumber).getBlock();
      blockDbOperations.storeBlock(block);
      output = "Block " + blockNumber + " with hash " + block.getHash() + " processed";
    }
    else
    {
      output = "Block " + String.valueOf(blockNumber) + " exists in persistance";
    }
    return output;
  }

}
