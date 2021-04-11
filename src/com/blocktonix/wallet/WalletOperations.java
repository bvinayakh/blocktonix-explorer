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


  public WalletOperations()
  {
    try
    {
      EthGetBalance ethBalance = Constants.web3.ethGetBalance("0xe013d04a4679e364e3d38db30b3d4c55da3bdc2d", DefaultBlockParameterName.LATEST).send();
      BigInteger wei = ethBalance.getBalance();
      System.out.println(Convert.fromWei(wei.toString(), Unit.ETHER));

    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void getBalance()
  {
    System.out.println("balance");
  }

  public static void main(String[] args)
  {
    
  }
}
