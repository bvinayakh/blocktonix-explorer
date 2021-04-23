package com.blocktonix.main;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import com.blocktonix.block.BlockOperations;
import com.blocktonix.block.dao.BlockDBOperations;

public class ForwardSync implements Runnable
{
  public static final Logger logger = LoggerFactory.getLogger(ForwardSync.class);
  
  private List<BigInteger> forwardBlockNumbersList = null;

  private BlockOperations blockOps = new BlockOperations();
  private BlockDBOperations blockDbOperations = new BlockDBOperations();

  public ForwardSync(List<BigInteger> blocksList)
  {
    this.forwardBlockNumbersList = blocksList;
  }

  @Override
  public void run()
  {
    for (BigInteger blockNumber : forwardBlockNumbersList)
    {
      Block block;
      try
      {
        logger.info("Fetching forward block : "+ blockNumber);
        block = blockOps.getBlock(blockNumber).getBlock();
        blockDbOperations.storeBlock(block);
      }
      catch (IOException e)
      {
        System.err.println("Exception in getting block information " + e.getMessage());
      }
    }
  }
}
