package com.blocktonix.contract.dao;

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import com.blocktonix.dao.DBSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ContractDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(ContractDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;


  public ContractDBOperations()
  {
    // session = DBSession.getSessionFactory().openSession();
    mapper = new ObjectMapper();
  }

  public void storeContract(JsonNode contractNode)
  {
    ContractDao dao = new ContractDao();
    dao.decimals = contractNode.get("Decimals").asText();
    dao.symbol = contractNode.get("Symbol").asText();
    dao.totalSupply = contractNode.get("TotalSupply").asText();
    dao.name = contractNode.get("Name").asText();
    dao.address = contractNode.get("ContractAddress").asText();
    dao.from = contractNode.get("TransferFrom").asText();
    dao.to = contractNode.get("TransferTo").asText();
    dao.amount = contractNode.get("Amount").asText();
    dao.transactionHash = contractNode.get("TransactionHash").asText();
    dao.blockNumber = contractNode.get("Block").asText();
    dao.abi = contractNode.get("ABI").asText();
    Session session = DBSession.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    if (session != null) session.close();

    logger.info("stored contract " + contractNode.get("Symbol").asText() + " with amount " + dao.amount + " with transaction hash "
        + contractNode.get("TransactionHash").asText() + " in block " + contractNode.get("Block").asText());
  }

  public void storeContractAbi(String contractAddress, String contractSymbol, String contractAbi)
  {
    ContractABIDao dao = new ContractABIDao();
    dao.contractAbi = contractAbi;
    dao.contractAddress = contractAddress;
    dao.contractSymbol = contractSymbol;
    Session session = DBSession.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    if (session != null) session.close();
    logger.info("stored contract ABI for " + contractSymbol);
  }

  public String getContractAbi(String contractAddress)
  {
    String contractAbi = null;
    Session session = DBSession.getSessionFactory().openSession();
    EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ContractABIDao> q = cb.createQuery(ContractABIDao.class);
    // select * equivalent
    Root<ContractABIDao> c = q.from(ContractABIDao.class);
    TypedQuery<ContractABIDao> query = entityManager.createQuery(q);
    List<ContractABIDao> resultList = query.getResultList();
    Iterator<ContractABIDao> contractAbiIterator = resultList.iterator();
    while (contractAbiIterator.hasNext())
    {
      ContractABIDao dao = contractAbiIterator.next();
      if (dao.contractAddress.equalsIgnoreCase(contractAddress)) contractAbi = dao.contractAbi;
    }
    entityManager.close();
    if (session != null) session.close();
    return contractAbi;
  }
}
