package com.blocktonix.main;

import java.math.BigInteger;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.block.BlockTaskRunnable;
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
      System.out.println("processing forward block number : " + blockNumber);
      processBlock(blockNumber);
      try
      {
        Thread.sleep(10000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }

  private void processBlock(BigInteger blockNumber)
  {
    new Thread(new BlockTaskRunnable(Constants.web3, blockNumber)).start();
  }
}
