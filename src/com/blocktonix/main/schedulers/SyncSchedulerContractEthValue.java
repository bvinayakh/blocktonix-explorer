package com.blocktonix.main.schedulers;

import java.text.ParseException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.contract.ContractDBOperations;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SyncSchedulerContractEthValue implements Job
{

  public static final Logger logger = LoggerFactory.getLogger(SyncSchedulerContractEthValue.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException
  {
    ContractDBOperations contractDb = new ContractDBOperations();
    try
    {
      contractDb.calculateContractEthValue();
    }
    catch (JsonProcessingException e)
    {
      e.printStackTrace();
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    catch (NullPointerException e)
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
