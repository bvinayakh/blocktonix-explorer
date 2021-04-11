package com.blocktonix.transaction;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import com.blocktonix.utils.Constants;

public class TransactionOperations
{
  public EthGetTransactionCount getTransactionCount(String ethAddress) throws InterruptedException, ExecutionException, IOException
  {
    return Constants.web3.ethGetTransactionCount(ethAddress, DefaultBlockParameter.valueOf("latest")).send();
  }

  public EthTransaction getTransaction(String transactionHash) throws InterruptedException, ExecutionException, IOException
  {
    return Constants.web3.ethGetTransactionByHash(transactionHash).send();
  }

  public EthGetTransactionReceipt getTransactionReceipt(String transactionHash) throws InterruptedException, ExecutionException, IOException
  {
    return Constants.web3.ethGetTransactionReceipt(transactionHash).send();
  }
}
