package com.blocktonix.main.schedulers;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.contract.TokenDBOperations;
import com.blocktonix.utils.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SyncSchedulerCoinsGecko implements Job
{

  public static final Logger logger = LoggerFactory.getLogger(SyncSchedulerCoinsGecko.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException
  {
    try
    {
      TokenDBOperations tokenOperations = new TokenDBOperations();
      tokenOperations.storeTokenInfo(Utilities.getCoinsList());
    }
    catch (JsonProcessingException e)
    {
      e.printStackTrace();
    }
  }

  static void printLongerTrace(Throwable t)
  {
    for (StackTraceElement e : t.getStackTrace())
      System.out.println(e);
  }
}
