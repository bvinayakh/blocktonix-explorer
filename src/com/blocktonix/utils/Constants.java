package com.blocktonix.utils;

import java.util.ArrayList;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Constants
{
  public static String blockchainEndpoint = "http://135.181.213.174:8545";

  public static Web3j web3 = Web3j.build(new HttpService(blockchainEndpoint));
  
  public static ArrayList<String> contractTransactionsInputList = new ArrayList<String>();
  
  static
  {
    contractTransactionsInputList.add("0x095ea7b3");
    contractTransactionsInputList.add("0xa9059cbb");
  }
}
