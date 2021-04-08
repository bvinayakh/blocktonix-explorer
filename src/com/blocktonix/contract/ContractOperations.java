package com.blocktonix.contract;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContractOperations
{
  public static final Logger logger = LoggerFactory.getLogger(ContractOperations.class);

  private Web3j web3 = null;
  private Credentials creds = Credentials.create("b1427aa43d62f7592feff8874d20fa42d9dbc96005d2a1c8a5cfdd55ebbfca62");

  private ObjectMapper mapper = null;

  public ContractOperations(Web3j web3j)
  {
    this.web3 = web3j;
    mapper = new ObjectMapper();
  }

  public String getContractABI(String contractAddress) throws IOException
  {
    String etherscanAPI =
        "https://api.etherscan.io/api?module=contract&action=getabi&address=" + contractAddress + "&apikey=" + "J5WK7PZUEN9IA3F8HTKY74FDVWD6IDQK7N";
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(etherscanAPI).build();
    Response response = client.newCall(request).execute();
    logger.info("contract ABI found for " + contractAddress);
    // System.out.println("contract ABI found for " + contractAddress);
    return response.body().string();
  }

  public JsonNode getContractInfo(String contractAddress)
  {

    ObjectNode node = mapper.createObjectNode();
    try
    {
      ERC20 javaToken = ERC20.load(contractAddress, web3, creds, new DefaultGasProvider());
      node.putPOJO("Decimals", javaToken.decimals().send());
      node.putPOJO("Symbol", javaToken.symbol().send());
      node.putPOJO("TotalSupply", javaToken.totalSupply().send());
      node.putPOJO("Name", javaToken.name().send());
    }
    catch (Exception e)
    {
      // System.err.println("Error fetching contract information for " + contractAddress + " error: " +
      // e.getMessage());
      logger.error("Error fetching contract information for " + contractAddress + " error: " + e.getMessage());
    }
    return node;
  }

  public static void main(String[] args)
  {
    Web3j web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/3deb0847ce9942568005689574ba69db"));
    ContractOperations ops = new ContractOperations(web3);
    // System.out.println(ops.getContractABI("0x7A3C45F34eA5DF6fE5F6aF710eC8A04D388a71D1"));
    Credentials creds = Credentials.create("b1427aa43d62f7592feff8874d20fa42d9dbc96005d2a1c8a5cfdd55ebbfca62");

    try
    {
      System.out.println(web3.ethAccounts().send().getAccounts().get(0));
    }
    catch (IOException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    ERC20 javaToken = ERC20.load("0x7A3C45F34eA5DF6fE5F6aF710eC8A04D388a71D1", web3, creds, new DefaultGasProvider());
    try
    {
      System.out.println(javaToken.decimals().send());
      System.out.println(javaToken.symbol().send());
      System.out.println(javaToken.totalSupply().send());
      System.out.println(javaToken.name().send());
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
