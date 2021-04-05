package com.blocktonix.transaction;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;

public class TransactionOperations
{
  Web3j web3 = null;

  public TransactionOperations(Web3j web3j)
  {
    this.web3 = web3j;
  }

  public EthGetTransactionCount getTransactionCount(String ethAddress) throws InterruptedException, ExecutionException, IOException
  {
    // EthGetTransactionCount result = new EthGetTransactionCount();
    // result = web3.ethGetTransactionCount(ethAddress,
    // DefaultBlockParameter.valueOf("latest")).sendAsync().get();
    // return result;
    return web3.ethGetTransactionCount(ethAddress, DefaultBlockParameter.valueOf("latest")).send();
  }

  public EthTransaction getTransaction(String transactionHash) throws InterruptedException, ExecutionException, IOException
  {
    // EthTransaction result = new EthTransaction();
    // result = web3.ethGetTransactionByHash(transactionHash).sendAsync().get();
    // return result;
    return web3.ethGetTransactionByHash(transactionHash).send();
  }

  public EthGetTransactionReceipt getTransactionReceipt(String transactionHash) throws InterruptedException, ExecutionException, IOException
  {
    // EthGetTransactionReceipt result = new EthGetTransactionReceipt();
    // result = web3.ethGetTransactionReceipt(transactionHash).sendAsync().get();
    // return result;
    return web3.ethGetTransactionReceipt(transactionHash).send();
  }
}
