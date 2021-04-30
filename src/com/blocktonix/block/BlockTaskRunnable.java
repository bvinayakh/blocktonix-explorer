package com.blocktonix.block;

import java.io.IOException;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

public class BlockTaskRunnable implements Runnable
{
  public static final Logger logger = LoggerFactory.getLogger(BlockTaskRunnable.class);

  private BigInteger blockNumber;

  private BlockOperations blockOps = null;
  private BlockDBOperations blockDbOperations = null;

  public BlockTaskRunnable(Web3j web3, BigInteger blockNumber)
  {
    this.blockNumber = blockNumber;
    blockOps = new BlockOperations();
    blockDbOperations = new BlockDBOperations();
    System.out.println("processing historical block number :" + this.blockNumber);
  }

  @Override
  public void run()
  {
    if (!blockDbOperations.getBlocksInDb().contains(blockNumber))
    {
      try
      {
        Block block = blockOps.getBlock(blockNumber).getBlock();
        blockDbOperations.storeBlock(block);
      }
      catch (IOException e)
      {
        logger.error("Error processing block in Forward Sync : " + e.getMessage());
      }

    }
  }
}
