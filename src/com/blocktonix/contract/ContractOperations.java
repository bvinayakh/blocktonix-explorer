package com.blocktonix.contract;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

  public JsonNode getContractInfo(String contractAddress)
  {

    ObjectNode node = mapper.createObjectNode();
    try
    {
      ERC20 javaToken = ERC20.load(contractAddress, web3, creds, new DefaultGasProvider());
      String symbol = javaToken.symbol().send();
      node.putPOJO("Decimals", javaToken.decimals().send());
      node.putPOJO("Symbol", symbol);
      node.putPOJO("TotalSupply", javaToken.totalSupply().send());
      node.putPOJO("Name", javaToken.name().send());
      logger.info("Contract Info for " + contractAddress + " " + symbol);
    }
    catch (Exception e)
    {
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
      // System.out.println(ops.getContractABI("0x33e7e9bac33ee273d7c81de3fc772fc65364810f"));
      ops.decodeInput(
          "0xb11456bd000000000000000000000000a0b86991c6218b36c1d19d4a2e9eb0ce3606eb4800000000000000000000000000000000000000000000000000000002540be400000000000000000000000000626e8036deb333b408be468f951bdb42433cbf1800000000000000000000000000000000000000000000011cd9eb7f80c7a7162b00000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000003000000000000000000000000a0b86991c6218b36c1d19d4a2e9eb0ce3606eb480000000000000000000000000000000000000000000000000000000000000000000000000000000000000000626e8036deb333b408be468f951bdb42433cbf180000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000001a0000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000001200000000000000000000000000000000000000000000000000000000000000001000000000000000000000000057fa445b1856d800174f89daa5c025f32654a04000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000020000000000000000000000000b4e16d0168e52d35cacd2c6185b44281ec28c9dc000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000007d0000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000001200000000000000000000000000000000000000000000000000000000000000001000000000000000000000000057fa445b1856d800174f89daa5c025f32654a04000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000020000000000000000000000000f956cdc8f43637f34f4be368fa1cb7da01d622be000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000007d0");
    }
    catch (NoSuchMethodException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // try
    // {
    // System.out.println(web3.ethAccounts().send().getAccounts().get(0));
    // }
    // catch (IOException e1)
    // {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // ERC20 javaToken = ERC20.load("0x7A3C45F34eA5DF6fE5F6aF710eC8A04D388a71D1", web3, creds, new
    // DefaultGasProvider());
    // try
    // {
    // System.out.println(javaToken.decimals().send());
    // System.out.println(javaToken.symbol().send());
    // System.out.println(javaToken.totalSupply().send());
    // System.out.println(javaToken.name().send());
    // }
    // catch (Exception e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

  public void decodeInput(String data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
  {
    String inputData = data;
    String method = inputData.substring(0, 10);
    System.out.println("Method >>>>>> " + method);
    String to = inputData.substring(10, 74);
    String value = inputData.substring(74);
    Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
    refMethod.setAccessible(true);
    Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
    System.out.println("Address>>>>>> " + address.toString());
    Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
    System.out.println("amount >>>>>> " + amount.getValue());
  }
}
