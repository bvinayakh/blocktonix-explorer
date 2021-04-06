package com.blocktonix.transaction.dao;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.blocktonix.dao.DBEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TransactionReceiptDBOperations
{
  private EntityManager entitymanager = null;
  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  public TransactionReceiptDBOperations()
  {
    entitymanager = DBEntity.getEntityManager();
    mapper = new ObjectMapper();

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
    entitymanager.close();
    return parentNode;
  }

  public void storeTransaction(TransactionReceipt receipt)
  {
    TransactionReceiptDao dao = new TransactionReceiptDao();
    dao.blockHash = receipt.getBlockHash();
    dao.blockNumber = String.valueOf(receipt.getBlockNumber());
    dao.contractAddress = receipt.getContractAddress();
    dao.cumulativeGasUsed = String.valueOf(receipt.getCumulativeGasUsed());
    dao.from = String.valueOf(receipt.getFrom());
    dao.gasUsed = String.valueOf(receipt.getGasUsed());
    List<Log> logsList = receipt.getLogs();
    Iterator<Log> logsIterator = logsList.iterator();
    while (logsIterator.hasNext())
    {
      Log log = logsIterator.next();
      dao.logsAddress = log.getAddress();
      dao.logsData = log.getData();
      dao.logsLogIndex = String.valueOf(log.getLogIndex());
      dao.logsTopics = StringUtils.join(log.getTopics(), ",");
      dao.logsType = log.getType();
    }
    dao.logsBloom = receipt.getLogsBloom();
    dao.status = receipt.getStatus();
    dao.to = receipt.getTo();
    dao.transactionHash = receipt.getTransactionHash();
    dao.transactionIndex = String.valueOf(receipt.getTransactionIndex());
    dao.transactionStatus = receipt.getStatus();
    entitymanager.getTransaction().begin();
    entitymanager.persist(dao);
    entitymanager.getTransaction().commit();
    entitymanager.close();
    System.out.println("stored transaction receipt " + receipt.getTransactionHash() + " for transaction " + receipt.getTransactionHash() + " from block "
        + receipt.getBlockNumber());
  }

}
