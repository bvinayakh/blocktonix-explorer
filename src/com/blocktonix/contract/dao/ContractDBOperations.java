package com.blocktonix.contract.dao;

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import com.blocktonix.dao.DBEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ContractDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(ContractDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;

  private Web3j web3 = null;

  private Session session = null;


  public ContractDBOperations(Web3j web3)
  {
    session = DBEntity.getSessionFactory().openSession();
    mapper = new ObjectMapper();

    this.web3 = web3;
  }

  public void storeContract(JsonNode contractNode) throws Exception
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

    // entitymanager.getTransaction().begin();
    // entitymanager.persist(dao);
    // entitymanager.getTransaction().commit();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    // session.close();

    System.out.println("stored contract " + contractNode.get("Symbol").asText() + " with amount " + dao.amount + " with transaction hash "
        + contractNode.get("TransactionHash").asText() + " in block " + contractNode.get("Block").asText());
  }

  public void storeContractAbi(String contractAddress, String contractSymbol, String contractAbi)
  {
    ContractABIDao dao = new ContractABIDao();
    dao.contractAbi = contractAbi;
    dao.contractAddress = contractAddress;
    dao.contractSymbol = contractSymbol;

    // entitymanager.getTransaction().begin();
    // entitymanager.persist(dao);
    // entitymanager.getTransaction().commit();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    // session.close();
  }

  public String getContractAbi(String contractAddress) throws Exception
  {
    String contractAbi = null;
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
    return contractAbi;
  }
}
