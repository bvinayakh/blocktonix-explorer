package com.blocktonix.contract.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import com.blocktonix.dao.DBSession;
import com.fasterxml.jackson.databind.JsonNode;

public class SwapDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(SwapDBOperations.class);

  public SwapDBOperations()
  {}

  public void storeContract(JsonNode swapNode)
  {
    SwapDao dao = new SwapDao();
    dao.toAddress = swapNode.get("ToAddress").asText();
    dao.tokensIn = swapNode.get("TokensIn").asText();
    dao.tokensOut = swapNode.get("TokensOut").asText();
    Session session = DBSession.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    if (session != null) session.close();
    logger.info("stored swap for : " + dao.toAddress);
  }
}
