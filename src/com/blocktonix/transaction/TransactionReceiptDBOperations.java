package com.blocktonix.transaction;

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
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.blocktonix.dao.DBSession;
import com.blocktonix.dao.TransactionDao;
import com.blocktonix.dao.TransactionReceiptDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TransactionReceiptDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(TransactionReceiptDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  public TransactionReceiptDBOperations()
  {
    mapper = new ObjectMapper();
  }

  public JsonNode getBlock(String reportId) throws JsonProcessingException, IOException
  {
    parentNode = mapper.createObjectNode();
    Session session = DBSession.getSessionFactory().openSession();
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
    if (session != null) session.close();
    return parentNode;
  }

  public void storeTransactionReceipt(TransactionReceipt receipt)
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
    Session session = DBSession.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    if (session != null) session.close();
    logger.info("stored transaction receipt " + receipt.getTransactionHash() + " for transaction " + receipt.getTransactionHash() + " from block "
        + receipt.getBlockNumber());
  }

}
