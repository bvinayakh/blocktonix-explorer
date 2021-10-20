package com.blocktonix.contract;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.dao.BlockDao;
import com.blocktonix.dao.ContractABIDao;
import com.blocktonix.dao.ContractDao;
import com.blocktonix.dao.ContractEthValueDao;
import com.blocktonix.dao.DBSession;
import com.blocktonix.dao.TokensDao;
import com.blocktonix.utils.Utilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ContractDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(ContractDBOperations.class);

  private ObjectMapper mapper = null;
  // private ObjectNode parentNode = null;

  SessionFactory factory = DBSession.getSessionFactory();


  public ContractDBOperations()
  {
    // session = DBSession.getSessionFactory().openSession();
    mapper = new ObjectMapper();
  }

  public void storeContract(JsonNode contractNode)
  {
    ContractDao dao = new ContractDao();
    ObjectNode node = mapper.convertValue(contractNode, ObjectNode.class);
    dao.decimals = node.get("Decimals").asText();
    dao.symbol = node.get("Symbol").asText();
    dao.totalSupply = node.get("TotalSupply").asText();
    dao.name = node.get("Name").asText();
    dao.address = node.get("ContractAddress").asText();
    dao.from = node.get("TransferFrom").asText();
    dao.to = node.get("TransferTo").asText();
    dao.amount = node.get("Amount").asText();
    dao.transactionHash = node.get("TransactionHash").asText();
    dao.blockNumber = node.get("Block").asText();
    dao.abi = node.get("ABI").asText();
    Session session = DBSession.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    if (session != null) session.close();
    logger.info(
        "stored contract " + dao.symbol + " with amount " + dao.amount + " with transaction hash " + dao.transactionHash + " in block " + dao.blockNumber);
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


  public void calculateContractEthValue() throws JsonMappingException, JsonProcessingException, ParseException
  {
    Session session = factory.openSession();

    int pageNumber = 1;
    int pageSize = 1000;
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    countQuery.select(criteriaBuilder.count(countQuery.from(ContractDao.class)));
    Long count = session.createQuery(countQuery).getSingleResult();

    CriteriaQuery<ContractDao> criteriaQuery = criteriaBuilder.createQuery(ContractDao.class);
    Root<ContractDao> root = criteriaQuery.from(ContractDao.class);
    CriteriaQuery<ContractDao> selectQuery = criteriaQuery.select(root);

    TypedQuery<ContractDao> typedQuery = session.createQuery(selectQuery);
    while (pageNumber < count.intValue())
    {
      typedQuery.setFirstResult(pageNumber - 1);
      typedQuery.setMaxResults(pageSize);

      for (ContractDao contract : typedQuery.getResultList())
      {
        ContractEthValueDao dao = new ContractEthValueDao();
        Session contractSession = DBSession.getSessionFactory().openSession();
        contractSession.beginTransaction();
        dao.contractAddress = contract.address;
        dao.contractAmount = contract.amount;
        dao.contractBlockNumber = contract.blockNumber;
        dao.contractSymbol = contract.symbol;
        dao.contractTransactionHash = contract.transactionHash;
        String blockTime = getBlockTime(contract.blockNumber);
        
        // dao.contractBlockTime = blockTime;
        // dao.contractBlockTime = convertDateString(blockTime);
        String coinId = getCoinGeckoCoinId(contract.symbol, contract.address);
        if (coinId != null)
        {
          ObjectNode ethValue = Utilities.getEquivalents(contract.address, coinId, blockTime);
          BigDecimal totalContractInEth =
              BigDecimal.valueOf(NumberUtils.toDouble(ethValue.get("eth").asText())).multiply(BigDecimal.valueOf(NumberUtils.toDouble(contract.amount)));
          BigDecimal totalContractInUsd =
              BigDecimal.valueOf(NumberUtils.toDouble(ethValue.get("usd").asText())).multiply(BigDecimal.valueOf(NumberUtils.toDouble(contract.amount)));
          if (ethValue.has("eth")) dao.contractValueEth = String.valueOf(totalContractInEth);
          if (ethValue.has("usd")) dao.contractValueUsd = String.valueOf(totalContractInUsd);
        }

        contractSession.save(dao);
        contractSession.getTransaction().commit();
        contractSession.close();
      }
      pageNumber += pageSize;
    }

    if (session != null) session.close();
  }

  public String getCoinGeckoCoinId(String contractSymbol, String ethContractAddress)
  {
    String coinId = null;
    String hql = "FROM Tokens t where t.coin_symbol = :contractSymbol and t.eth_address = :ethContractAddress";
    Session session = DBSession.getSessionFactory().openSession();
    Query<TokensDao> query = session.createQuery(hql);
    query.setParameter("contractSymbol", contractSymbol.toLowerCase().replaceAll("\"", ""));
    query.setParameter("ethContractAddress", ethContractAddress);
    List<TokensDao> tokenList = query.getResultList();
    logger.info("coingecko contract symbol: " + contractSymbol);
    logger.info("coingeck eth contract address: " + ethContractAddress);
    logger.info("coingecko tokens list size: " + tokenList.size());
    if (tokenList.size() > 0) coinId = tokenList.get(0).coin_id;
    return coinId;
  }

  public Date convertDateString(String date) throws ParseException
  {
    // SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    // inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    // return inputFormat.parse(date.toString());
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    OffsetDateTime odtInstanceAtOffset = OffsetDateTime.parse(date, DATE_TIME_FORMATTER);
    OffsetDateTime odtInstanceAtUTC = odtInstanceAtOffset.withOffsetSameInstant(ZoneOffset.UTC);
    String dateStringInUTC = odtInstanceAtUTC.format(DATE_TIME_FORMATTER);
    return Date.from(odtInstanceAtUTC.toInstant());
  }

  public String getBlockTime(String blockNumber)
  {
    String hql = "FROM Block b where b.number = :blockNumber";
    Session session = DBSession.getSessionFactory().openSession();
    Query<BlockDao> query = session.createQuery(hql);
    query.setParameter("blockNumber", blockNumber);
    Long timestamp = Long.valueOf(query.getResultList().get(0).timestamp);
    java.util.Date date = new java.util.Date((long) timestamp * 1000);
    if (session != null) session.close();


    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    String formattedDate = null;
    formattedDate = inputFormat.format(date);
    return formattedDate;
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
