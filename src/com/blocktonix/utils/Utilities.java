package com.blocktonix.utils;

import java.util.concurrent.ExecutionException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;

public class Utilities
{
  Web3j web3 = null;

  public Utilities(Web3j web3j)
  {
    this.web3 = web3j;
  }

  public EthAccounts getEthAccounts() throws InterruptedException, ExecutionException
  {
    EthAccounts result = new EthAccounts();
    result = web3.ethAccounts().sendAsync().get();
    return result;
  }

  public EthGetBalance getEthBalance(String ethAddress) throws InterruptedException, ExecutionException
  {
    EthGetBalance result = new EthGetBalance();
    result = web3.ethGetBalance(ethAddress, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
    return result;
  }

}
