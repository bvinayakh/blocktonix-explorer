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
import org.web3j.tx.gas.DefaultGasProvider;
import com.blocktonix.utils.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ContractOperations
{
  public static final Logger logger = LoggerFactory.getLogger(ContractOperations.class);

  private Credentials creds = Credentials.create("b1427aa43d62f7592feff8874d20fa42d9dbc96005d2a1c8a5cfdd55ebbfca62");

  private ObjectMapper mapper = null;

  public ContractOperations()
  {
    mapper = new ObjectMapper();
  }

  public JsonNode getContractInfo(String contractAddress)
  {

    ObjectNode node = mapper.createObjectNode();
    try
    {
      ERC20 javaToken = ERC20.load(contractAddress, Constants.web3, creds, new DefaultGasProvider());
      String symbol = javaToken.symbol().send();
      node.putPOJO("Decimals", javaToken.decimals().send());
      node.putPOJO("Symbol", symbol);
      node.putPOJO("TotalSupply", javaToken.totalSupply().send());
      node.putPOJO("Name", javaToken.name().send());
      logger.debug("Contract Info for " + contractAddress + " " + symbol);
    }
    catch (Exception e)
    {
      logger.error("Error fetching contract information for " + contractAddress + " error: " + e.getMessage());
    }
    return node;
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
