package com.blocktonix.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utilities
{
  public static final Logger logger = LoggerFactory.getLogger(Utilities.class);

  private static List<String> swapRoutersList = new ArrayList<String>();

  private static ObjectMapper mapper = new ObjectMapper();

  static
  {
    // sushi swap
    swapRoutersList.add("0xd9e1ce17f2641f24ae83637ab66a2cca9c378b9f");
    // uniswap
    swapRoutersList.add("0x7a250d5630b4cf539739df2c5dacb4c659f2488d");
  }

  public static EthAccounts getEthAccounts() throws InterruptedException, ExecutionException
  {
    EthAccounts result = new EthAccounts();
    result = Constants.web3.ethAccounts().sendAsync().get();
    return result;
  }

  public static EthGetBalance getEthBalance(String ethAddress) throws InterruptedException, ExecutionException
  {
    EthGetBalance result = new EthGetBalance();
    result = Constants.web3.ethGetBalance(ethAddress, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
    return result;
  }
  
  public static void getEthValue()
  {
  }

  public static String getContractABI(String contractAddress)
  {
    String etherscanAPI =
        "https://api.etherscan.io/api?module=contract&action=getabi&address=" + contractAddress + "&apikey=" + "J5WK7PZUEN9IA3F8HTKY74FDVWD6IDQK7N";
    String responseValue = null;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(etherscanAPI).build();
    Response response;
    try
    {
      response = client.newCall(request).execute();
      responseValue = response.body().string();
    }
    catch (IOException e)
    {
      logger.error("IOException getting Contract ABI from Etherscan " + e.getLocalizedMessage());
    }
    logger.info("Contract ABI from Etherscan for " + contractAddress);
    return responseValue;
  }

  public static List<String> get4ByteAPI(String hexSignature) throws IOException
  {
    String byteAPI = "https://www.4byte.directory/api/v1/signatures/?hex_signature=" + hexSignature;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(byteAPI).build();
    Response response = client.newCall(request).execute();
    String responseOutput = response.body().string();
    JsonNode responseNode = mapper.readTree(responseOutput);
    JsonNode resultsNode = responseNode.get("results");
    Iterator<JsonNode> resultsIterator = resultsNode.iterator();
    List<String> methods = new ArrayList<>();
    while (resultsIterator.hasNext())
    {
      methods.add(resultsIterator.next().get("text_signature").asText());
    }
    return methods;
  }

  public static LinkedList<String> split64(String data)
  {
    LinkedList<String> strings = new LinkedList<>();
    int index = 0;
    while (index < data.length())
    {
      strings.add(data.substring(index, Math.min(index + 64, data.length())));
      index += 64;
    }

    return strings;
  }

  public static boolean isSwapRouter(String contractAddress)
  {
    if (swapRoutersList.contains(contractAddress)) return true;
    else
      return false;
  }

}


