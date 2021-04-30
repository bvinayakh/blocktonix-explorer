package com.blocktonix.contract;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.dao.DBSession;
import com.blocktonix.dao.TokensDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class TokenDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(TokenDBOperations.class);

  private ObjectMapper mapper = null;


  public TokenDBOperations()
  {
    mapper = new ObjectMapper();
  }

  public void storeTokenInfo(String tokenInfo) throws JsonMappingException, JsonProcessingException
  {
    ArrayNode tokenInfoNode = (ArrayNode) mapper.readTree(tokenInfo);
    for (JsonNode node : tokenInfoNode)
    {
      try
      {
        TokensDao dao = new TokensDao();
        Session session = DBSession.getSessionFactory().openSession();
        session.beginTransaction();
        dao.coin_id = node.get("id").asText();
        dao.coin_symbol = node.get("symbol").asText();
        dao.coin_name = node.get("name").asText();
        session.saveOrUpdate(dao);
        session.getTransaction().commit();
        if (session != null) session.close();
      }
      catch (ConstraintViolationException e)
      {
        logger.info("Token " + node.get("symbol") + " already present in DB");
      }
    }
  }
}
