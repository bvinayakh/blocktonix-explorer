package com.blocktonix.contract.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import com.blocktonix.dao.DBSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SwapDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(SwapDBOperations.class);

  private ObjectMapper mapper = null;
  private ObjectNode parentNode = null;


  private Session session = null;


  public SwapDBOperations(Web3j web3)
  {
    session = DBSession.getSession();
    mapper = new ObjectMapper();
  }

  public void storeContract(JsonNode swapNode)
  {
    SwapDao dao = new SwapDao();
    dao.toAddress = swapNode.get("ToAddress").asText();
    dao.tokensIn = swapNode.get("TokensIn").asText();
    dao.tokensOut = swapNode.get("TokensOut").asText();

    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();

    logger.info("stored swap ");
  }
}
