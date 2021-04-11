package com.blocktonix.wallet;

import java.io.IOException;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import com.blocktonix.utils.Constants;

public class WalletOperations
{
  public static final Logger logger = LoggerFactory.getLogger(WalletOperations.class);

  public String getBalance(String walletAddress)
  {
    String balance = null;
    try
    {
      EthGetBalance ethBalance = Constants.web3.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).send();
      BigInteger wei = ethBalance.getBalance();
      balance = String.valueOf(Convert.fromWei(wei.toString(), Unit.ETHER));
    }
    catch (IOException e)
    {
      System.err.println("Error retrieving ETH balance for wallet address " + walletAddress);
    }
    return balance;
  }

  public static void main(String[] args)
  {
    new WalletOperations();
  }
}
