package com.blocktonix.transaction.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.utils.Numeric;
import com.blocktonix.contract.ContractOperations;
import com.blocktonix.contract.dao.ContractDBOperations;
import com.blocktonix.dao.DBEntity;
import com.blocktonix.utils.Utilities;
import com.blocktonix.wallet.WalletOperations;
import com.blocktonix.wallet.dao.WalletDBOperations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TransactionDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(TransactionDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  private ContractOperations contractOps = null;
  private ContractDBOperations contractDbOps = null;
  private WalletOperations walletOps = null;
  private WalletDBOperations walletDbOps = null;

  private Session session = null;

  public TransactionDBOperations()
  {
    mapper = new ObjectMapper();

    contractOps = new ContractOperations();
    contractDbOps = new ContractDBOperations();

    walletOps = new WalletOperations();
    walletDbOps = new WalletDBOperations();

    session = DBEntity.getSessionFactory().openSession();
  }

  public JsonNode getBlock(String reportId) throws JsonProcessingException, IOException
  {
    parentNode = mapper.createObjectNode();
    EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TransactionDao> criteria = criteriaBuilder.createQuery(TransactionDao.class);
    // select * equivalent
    Root<TransactionDao> selectAll = criteria.from(TransactionDao.class);

    ParameterExpression<String> queryParameters = criteriaBuilder.parameter(String.class);
    criteria.select(selectAll).where(criteriaBuilder.equal(selectAll.get("tid"), queryParameters));

    TypedQuery<TransactionDao> query = entityManager.createQuery(criteria);
    query.setParameter(queryParameters, reportId);

    List<TransactionDao> resultList = query.getResultList();
    Iterator<TransactionDao> resultIterator = resultList.iterator();
    while (resultIterator.hasNext())
    {
      TransactionDao resultDao = resultIterator.next();
    }
    entityManager.close();
    return parentNode;
  }

  public String storeTransaction(TransactionObject transaction)
  {
    try
    {
      TransactionDao dao = new TransactionDao();
      dao.blockHash = transaction.getBlockHash();
      dao.blockNumber = String.valueOf(transaction.getBlockNumber());
      dao.from = String.valueOf(transaction.getFrom());
      dao.gas = String.valueOf(transaction.getGas());
      dao.gasPrice = String.valueOf(transaction.getGasPrice());
      dao.hash = transaction.getHash();
      String transactionInput = transaction.getInput();
      dao.input = transactionInput;
      if (transactionInput.length() > 20)
      {
        JsonNode inputNode = decodeInput(transactionInput, transaction.getHash());
        if (inputNode != null)
        {
          String contractAddress = transaction.getTo();
          ObjectNode contractNode = mapper.createObjectNode();
          JsonNode contractInfoNode = null;
          if (!Utilities.isSwapRouter(contractAddress)) contractInfoNode = contractOps.getContractInfo(contractAddress);
          // dont store contract
          // Contract Call has been reverted by the EVM with the reason: 'execution reverted'.
          if ((contractInfoNode != null) & (contractInfoNode.size() > 0))
          {
            contractNode.putPOJO("Decimals", contractInfoNode.get("Decimals"));
            contractNode.putPOJO("Symbol", contractInfoNode.get("Symbol"));
            contractNode.putPOJO("TotalSupply", contractInfoNode.get("TotalSupply"));
            contractNode.putPOJO("Name", contractInfoNode.get("Name"));
            contractNode.putPOJO("ContractAddress", contractAddress);
            contractNode.putPOJO("TransferFrom", transaction.getFrom());
            contractNode.putPOJO("TransferTo", inputNode.get("Address").asText());
            // contractNode.putPOJO("Amount", inputNode.get("Amount").asText());
            String xferAmountString = String.valueOf(Numeric.toBigInt(inputNode.get("Amount").asText()));
            Integer decimalsInt = Integer.valueOf(contractInfoNode.get("Decimals").asText());
            String preRoundedAmount = calculateDecimalPlaces(transaction.getHash(), contractAddress, xferAmountString, '.', decimalsInt);
            String amountRounded = String.valueOf(roundAvoid(Double.valueOf(preRoundedAmount), 3));
            contractNode.putPOJO("Amount", amountRounded);
            contractNode.putPOJO("TransactionHash", transaction.getHash());
            contractNode.putPOJO("Block", transaction.getBlockNumber());
            // search for contractAbi in database. if not found then get from etherscan
            String contractAbi = contractDbOps.getContractAbi(contractAddress);
            if (contractAbi == null)
            {
              logger.info("Fetching Contract ABI for contract " + contractAddress + " from Etherscan");
              contractAbi = Utilities.getContractABI(contractAddress);
              contractDbOps.storeContractAbi(contractAddress, contractInfoNode.get("Symbol").asText(), contractAbi);
            }
            contractNode.putPOJO("ABI", contractAbi);
            contractDbOps.storeContract(contractNode);

            logger.info("Storing Wallet ETH Balance for wallet " + transaction.getFrom());
            walletDbOps.storeWalletBalanceETH(transaction.getFrom(), walletOps.getBalance(transaction.getFrom()));

            logger.info("Storing Wallet ETH Balance for wallet " + inputNode.get("Address").asText());
            walletDbOps.storeWalletBalanceETH(inputNode.get("Address").asText(), walletOps.getBalance(inputNode.get("Address").asText()));
          }
          dao.nonce = String.valueOf(transaction.getNonce());
          dao.r = transaction.getR();
          dao.s = transaction.getS();
          dao.to = transaction.getTo();
          dao.transactionIndex = String.valueOf(transaction.getTransactionIndex());
          dao.v = String.valueOf(transaction.getV());
          dao.value = String.valueOf(transaction.getValue());

          session.beginTransaction();
          session.save(dao);
          session.getTransaction().commit();
          logger.debug("stored transaction " + transaction.getHash() + " from block " + transaction.getBlockNumber());
        }
      }
    }
    catch (ConstraintViolationException constraintException)
    {
      logger.error("Entry exists for Contract " + transaction.getTo() + " in transaction " + transaction.getHash());
    }
    return transaction.getHash();
  }

  public String calculateDecimalPlaces(String transferHash, String contractAddress, String str, char ch, int position)
  {
    String output = "not found";
    try
    {
      if (!str.equalsIgnoreCase("0"))
      {
        str = StringUtils.reverse(str);
        output = StringUtils.reverse(str.substring(0, position) + ch + str.substring(position));
      }
      else
        output = str;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      logger.error("Error decoding the transfer amount for contract " + contractAddress + " in transaction " + transferHash);
    }
    catch (StringIndexOutOfBoundsException e)
    {
      logger.error("Error decoding the transfer amount for contract " + contractAddress + " in transaction " + transferHash);
    }
    return output;
  }

  // private JsonNode decodeInput(String data) throws NoSuchMethodException,
  // InvocationTargetException, IllegalAccessException
  // {
  // ObjectNode inputNode = mapper.createObjectNode();
  // String method = data.substring(0, 10);
  // inputNode.putPOJO("Method", method);
  // String to = data.substring(10, 74);
  // String value = data.substring(74);
  // Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class,
  // Class.class);
  // refMethod.setAccessible(true);
  // Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
  // // System.out.println("Address>>>>>> " + address.toString());
  // inputNode.putPOJO("Address", address.toString());
  // Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);
  // // System.out.println("amount >>>>>> " + amount.getValue());
  // inputNode.putPOJO("Amount", amount.getValue());
  // return inputNode;
  // }

  private double roundAvoid(double value, int places)
  {
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }

  private JsonNode decodeInput(String data, String txnHash)
  {
    // boolean isValidContract = false;
    ObjectNode inputNode = null;
    String method = data.substring(0, 10);
    // List<String> methodAction = Utilities.get4ByteAPI(method);
    // Iterator<String> methodActionsIterator = methodAction.iterator();
    // while (methodActionsIterator.hasNext())
    // {
    // String action = methodActionsIterator.next();
    // if ((action.toLowerCase().contains("transfer")) || (action.toLowerCase().contains("swap")) ||
    // (action.toLowerCase().contains("buy")))
    // isValidContract = true;
    // }
    // if (isValidContract)
    // {
    LinkedList<String> split64 = Utilities.split64(data.replace(method, ""));
    switch (method)
    {
      // valid
      case "0xa9059cbb":
        logger.info("processing transaction method 0xa9059cbb");
        inputNode = mapper.createObjectNode();
        inputNode.putPOJO("Method", method);
        data = StringUtils.replace(data, "0xa9059cbb", "");
        String transferedToAddress = "0x" + StringUtils.substring(data, 24, 64);
        String transferAmount = StringUtils.substring(data, 64, 128);
        inputNode.putPOJO("Address", transferedToAddress);
        inputNode.putPOJO("Amount", transferAmount);

        break;
      // swap eth for tokens
      case "0x7ff36ab5":
        logger.info("Method swapExactETHForTokens(uint256,address[],address,uint256)");
        break;
      case "0xfb3bdb41":
        logger.info("Method swapETHForExactTokens(uint256,address[],address,uint256)");
        break;
      case "0x18cbafe5":
        logger.info("Method swapExactTokensForETH(uint256,uint256,address[],address,uint256)");
        break;
      case "0x38ed1739":
        logger.info("Method swapExactTokensForTokens(uint256,uint256,address[],address,uint256)");
        System.out.println(split64);
        break;
      // sell to uniswap
      case "0xd9627aa4":
        break;
      case "0x683fa88d":
        logger.info("Method transferFrom(address,address,uint256)");
        break;
      case "0x791ac947":
        break;
      case "0xa694fc3a":
        break;
      default:
        logger.info("transaction not a contract sell or swap");
        logger.info(data);
        break;
    }
    return inputNode;
  }
}
