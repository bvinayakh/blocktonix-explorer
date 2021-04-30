package com.blocktonix.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.blocktonix.dao.BlockDao;
import com.blocktonix.dao.DBSession;
import com.blocktonix.transaction.TransactionDBOperations;
import com.blocktonix.transaction.TransactionOperations;
import com.blocktonix.transaction.TransactionReceiptDBOperations;
import com.blocktonix.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BlockDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(BlockDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  private TransactionOperations transactionOps = null;
  private TransactionDBOperations transactionDbOps = null;
  private TransactionReceiptDBOperations transactionReceiptDbOps = null;

  public BlockDBOperations()
  {
    mapper = new ObjectMapper();

    // session = DBSession.getSessionFactory().openSession();

    transactionOps = new TransactionOperations();
    transactionDbOps = new TransactionDBOperations();
    transactionReceiptDbOps = new TransactionReceiptDBOperations();
  }

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

    Session session = DBSession.getSessionFactory().openSession();
    List<String> blockInDb = new ArrayList<>();
    EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlockDao> criteriaQuery = criteriaBuilder.createQuery(BlockDao.class);
    Root<BlockDao> from = criteriaQuery.from(BlockDao.class);
    // CriteriaQuery<BlockDao> select = criteriaQuery.select(from);
    criteriaQuery.orderBy(criteriaBuilder.desc(from.get("number")));
    TypedQuery<BlockDao> query = entityManager.createQuery(criteriaQuery);
    // List<BlockDao> blockList = query.getResultList();
    List<BlockDao> blocksList = query.getResultList();
    entityManager.close();
    Iterator<BlockDao> blockIterator = blocksList.iterator();
    while (blockIterator.hasNext())
      blockInDb.add(blockIterator.next().number);
    return blockInDb;
  }

  public void storeBlock(Block block) throws IOException
  {
    Session session = DBSession.getSessionFactory().openSession();
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
    StringBuffer txns = new StringBuffer();
    dao.totalDifficulty = String.valueOf(block.getTotalDifficulty());
    List<TransactionResult> transactionsList = block.getTransactions();
    Iterator<TransactionResult> txnIter = transactionsList.iterator();
    while (txnIter.hasNext())
    {
      TransactionObject result = (TransactionObject) txnIter.next().get();
      txns.append(result.getHash());
      txns.append(",");
    }
    dao.transactions = txns.toString();
    dao.transactionsRoot = block.getTransactionsRoot();
    dao.uncles = StringUtils.join(block.getUncles(), ",");

    // persisting block
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    session.close();
    logger.info("stored block " + block.getNumber());
    // System.out.println("stored block " + block.getNumber());
    // block persisted

    // iterate txn and txn receipt
    Iterator<TransactionResult> transactionsIterator = transactionsList.iterator();
    while (transactionsIterator.hasNext())
    {
      TransactionObject transaction = (TransactionObject) transactionsIterator.next().get();
      String txhash = transactionDbOps.storeTransaction(transaction);
      TransactionReceipt receipt = Constants.web3.ethGetTransactionReceipt(txhash).send().getResult();
      transactionReceiptDbOps.storeTransactionReceipt(receipt);
    }


  }
}
