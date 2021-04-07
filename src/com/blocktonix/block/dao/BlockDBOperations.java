package com.blocktonix.block.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.blocktonix.dao.DBEntity;
import com.blocktonix.transaction.TransactionOperations;
import com.blocktonix.transaction.dao.TransactionDBOperations;
import com.blocktonix.transaction.dao.TransactionReceiptDBOperations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BlockDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(BlockDBOperations.class);

  private EntityManager entitymanager = null;
  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  private Web3j web3 = null;

  private TransactionOperations transactionOps = null;
  private TransactionDBOperations transactionDbOps = null;
  private TransactionReceiptDBOperations transactionReceiptDbOps = null;


  public BlockDBOperations(Web3j web3)
  {
    entitymanager = DBEntity.getEntityManager();

    mapper = new ObjectMapper();

    this.web3 = web3;

    transactionOps = new TransactionOperations(web3);
    transactionDbOps = new TransactionDBOperations(web3);
    transactionReceiptDbOps = new TransactionReceiptDBOperations();


  }

  // public JsonNode getBlock(BigInteger blockNumber) throws JsonProcessingException, IOException
  // {
  // parentNode = mapper.createObjectNode();
  // CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
  // CriteriaQuery<BlockDao> criteria = criteriaBuilder.createQuery(BlockDao.class);
  // // select * equivalent
  // Root<BlockDao> selectAll = criteria.from(BlockDao.class);
  //
  // ParameterExpression<String> queryParameters = criteriaBuilder.parameter(String.class);
  // criteria.select(selectAll).where(criteriaBuilder.equal(selectAll.get("tid"), queryParameters));
  //
  // TypedQuery<BlockDao> query = entitymanager.createQuery(criteria);
  // query.setParameter(queryParameters, reportId);
  //
  // List<BlockDao> resultList = query.getResultList();
  // Iterator<BlockDao> resultIterator = resultList.iterator();
  // while (resultIterator.hasNext())
  // {
  // BlockDao resultDao = resultIterator.next();
  // }
  // return parentNode;
  // }

  public List<String> getBlocksInDb()
  {
    // working
    // CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
    // CriteriaQuery<BlockDao> criteriaQuery = criteriaBuilder.createQuery(BlockDao.class);
    // Root<BlockDao> from = criteriaQuery.from(BlockDao.class);
    // // CriteriaQuery<BlockDao> select = criteriaQuery.select(from);
    // criteriaQuery.orderBy(criteriaBuilder.desc(from.get("number")));
    // TypedQuery<BlockDao> query = entitymanager.createQuery(criteriaQuery);
    // List<BlockDao> daoList = query.getResultList();
    // Iterator<BlockDao> daoIterator = daoList.iterator();
    // while (daoIterator.hasNext())
    // {
    // System.out.println(daoIterator.next().number);
    // }
    // working

    // String blockNumber = null;
    // CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
    // CriteriaQuery<BlockDao> criteriaQuery = criteriaBuilder.createQuery(BlockDao.class);
    // Root<BlockDao> from = criteriaQuery.from(BlockDao.class);
    // // CriteriaQuery<BlockDao> select = criteriaQuery.select(from);
    // criteriaQuery.orderBy(criteriaBuilder.desc(from.get("number")));
    // TypedQuery<BlockDao> query = entitymanager.createQuery(criteriaQuery);
    // List<BlockDao> blockList = query.getResultList();
    // if (blockList.size() > 0) blockNumber = query.getResultList().get(0).number;
    // return blockNumber;

    List<String> blockInDb = new ArrayList<>();
    CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
    CriteriaQuery<BlockDao> criteriaQuery = criteriaBuilder.createQuery(BlockDao.class);
    Root<BlockDao> from = criteriaQuery.from(BlockDao.class);
    // CriteriaQuery<BlockDao> select = criteriaQuery.select(from);
    criteriaQuery.orderBy(criteriaBuilder.desc(from.get("number")));
    TypedQuery<BlockDao> query = entitymanager.createQuery(criteriaQuery);
    // List<BlockDao> blockList = query.getResultList();
    List<BlockDao> blocksList = query.getResultList();
    // entitymanager.close();
    Iterator<BlockDao> blockIterator = blocksList.iterator();
    while (blockIterator.hasNext())
      blockInDb.add(blockIterator.next().number);
    return blockInDb;
  }

  public void storeBlock(Block block) throws Exception
  {
    BlockDao dao = new BlockDao();
    dao.difficulty = String.valueOf(block.getDifficulty());
    dao.extraData = block.getExtraData();
    dao.gasLimit = String.valueOf(block.getGasLimit());
    dao.gasUsed = String.valueOf(block.getGasUsed());
    dao.hash = block.getHash();
    dao.logsBloom = block.getLogsBloom();
    dao.miner = block.getMiner();
    dao.mixHash = block.getMixHash();
    dao.nonce = String.valueOf(block.getNonce());
    dao.number = String.valueOf(block.getNumber());
    dao.parentHash = block.getParentHash();
    dao.receiptsRoot = block.getReceiptsRoot();
    dao.sha3Uncles = block.getSha3Uncles();
    dao.size = String.valueOf(block.getSize());
    dao.stateRoot = block.getStateRoot();
    dao.timestamp = String.valueOf(block.getTimestamp());
    dao.totalDifficulty = String.valueOf(block.getTotalDifficulty());
    List<TransactionResult> transactionsList = block.getTransactions();
    Iterator<TransactionResult> transactionsIterator = transactionsList.iterator();
    while (transactionsIterator.hasNext())
    {
      TransactionObject transaction = (TransactionObject) transactionsIterator.next().get();
      String txhash = transactionDbOps.storeTransaction(transaction);
      TransactionReceipt receipt = web3.ethGetTransactionReceipt(txhash).send().getResult();
      transactionReceiptDbOps.storeTransaction(receipt);
    }
    dao.transactionsRoot = block.getTransactionsRoot();
    dao.uncles = StringUtils.join(block.getUncles(), ",");

    entitymanager.getTransaction().begin();
    entitymanager.persist(dao);
    entitymanager.getTransaction().commit();
    entitymanager.close();

    System.out.println("stored block " + block.getNumber());
  }
}
