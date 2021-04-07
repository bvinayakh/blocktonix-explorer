package com.blocktonix.transaction.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import com.blocktonix.contract.ContractOperations;
import com.blocktonix.contract.dao.ContractDBOperations;
import com.blocktonix.dao.DBEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TransactionDBOperations
{
  private EntityManager entitymanager = null;
  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  private ContractOperations contractOps = null;
  private ContractDBOperations contractDbOps = null;

  private Web3j web3 = null;

  public TransactionDBOperations(Web3j web3j)
  {
    entitymanager = DBEntity.getEntityManager();
    mapper = new ObjectMapper();

    this.web3 = web3j;
    contractOps = new ContractOperations(web3j);
    contractDbOps = new ContractDBOperations(web3j);
  }

  public JsonNode getBlock(String reportId) throws JsonProcessingException, IOException
  {
    parentNode = mapper.createObjectNode();
    CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
    CriteriaQuery<TransactionDao> criteria = criteriaBuilder.createQuery(TransactionDao.class);
    // select * equivalent
    Root<TransactionDao> selectAll = criteria.from(TransactionDao.class);

    ParameterExpression<String> queryParameters = criteriaBuilder.parameter(String.class);
    criteria.select(selectAll).where(criteriaBuilder.equal(selectAll.get("tid"), queryParameters));

    TypedQuery<TransactionDao> query = entitymanager.createQuery(criteria);
    query.setParameter(queryParameters, reportId);

    List<TransactionDao> resultList = query.getResultList();
    Iterator<TransactionDao> resultIterator = resultList.iterator();
    while (resultIterator.hasNext())
    {
      TransactionDao resultDao = resultIterator.next();
    }
    // entitymanager.close();
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
      // check for contract transfer within transaction
      if (transactionInput.startsWith("0xa9059cbb"))
      {
        String contractAddress = transaction.getTo();
        transactionInput = StringUtils.replace(transactionInput, "0xa9059cbb", "");
        String transferedToAddress = "0x" + StringUtils.substring(transactionInput, 24, 64);
        String transferAmount = StringUtils.substring(transactionInput, 64, 128);
        ObjectNode contractNode = mapper.createObjectNode();
        JsonNode contractInfoNode = contractOps.getContractInfo(contractAddress);
        contractNode.putPOJO("Decimals", contractInfoNode.get("Decimals"));
        contractNode.putPOJO("Symbol", contractInfoNode.get("Symbol"));
        contractNode.putPOJO("TotalSupply", contractInfoNode.get("TotalSupply"));
        contractNode.putPOJO("Name", contractInfoNode.get("Name"));
        contractNode.putPOJO("ContractAddress", contractAddress);
        contractNode.putPOJO("TransferFrom", transaction.getFrom());
        contractNode.putPOJO("TransferTo", transferedToAddress);
        // convert amount to readable decimal number
        String xferAmountString = String.valueOf(Numeric.toBigInt(transferAmount));
        Integer decimalsInt = Integer.valueOf(contractInfoNode.get("Decimals").asText());
        String preRoundedAmount = calculateDecimalPlaces(transaction.getHash(), contractAddress, xferAmountString, '.', decimalsInt);
        String amountRounded = preRoundedAmount.substring(0, preRoundedAmount.indexOf('.') + 3);
        // contractNode.putPOJO("Amount", calculateDecimalPlaces(transaction.getHash(), contractAddress,
        // xferAmountString, '.', decimalsInt));
        contractNode.putPOJO("Amount", amountRounded);
        contractNode.putPOJO("TransactionHash", transaction.getHash());
        contractNode.putPOJO("Block", transaction.getBlockNumber());
        // search for contractAbi in database. if not found then get from etherscan
        String contractAbi = contractDbOps.getContractAbi(contractAddress);
        if (contractAbi == null)
        {
          contractAbi = contractOps.getContractABI(contractAddress);
          contractDbOps.storeContractAbi(contractAddress, contractInfoNode.get("Symbol").asText(), contractAbi);
        }
        contractNode.putPOJO("ABI", contractAbi);
        contractDbOps.storeContract(contractNode);
      }
      dao.nonce = String.valueOf(transaction.getNonce());
      dao.r = transaction.getR();
      dao.s = transaction.getS();
      dao.to = transaction.getTo();
      dao.transactionIndex = String.valueOf(transaction.getTransactionIndex());
      dao.v = String.valueOf(transaction.getV());
      dao.value = String.valueOf(transaction.getValue());

      entitymanager.getTransaction().begin();
      entitymanager.persist(dao);
      entitymanager.getTransaction().commit();
      entitymanager.close();

      System.out.println("stored transaction " + transaction.getHash() + " from block " + transaction.getBlockNumber());
    }
    catch (ConstraintViolationException constraintException)
    {
      System.err.println("Entry exists for Contract " + transaction.getTo() + " in transaction " + transaction.getHash());
    }
    catch (Exception e)
    {
      System.err.println("Error in Contract ABI" + e.getMessage());
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
      System.err.println("Error decoding the transfer amount for contract " + contractAddress + " in transaction " + transferHash);
    }
    catch (StringIndexOutOfBoundsException e)
    {
      System.err.println("Error decoding the transfer amount for contract " + contractAddress + " in transaction " + transferHash);
    }
    return output;
  }
}
