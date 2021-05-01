package com.blocktonix.contract;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.dao.DBSession;
import com.blocktonix.dao.TokensDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        if (node.has("platforms"))
        {
          String platformString = node.toPrettyString();
          JsonNode platform = mapper.readTree(platformString).get("platforms");
          dao.coin_platforms = platform.toPrettyString();
          if (platform.has("ethereum")) dao.eth_address = mapper.readTree(platform.toPrettyString()).get("ethereum").toPrettyString().replace("\"", "");
          if (platform.has("binance-smart-chain"))
            dao.bsc_address = mapper.readTree(platform.toPrettyString()).get("binance-smart-chain").toPrettyString().replace("\"", "");
          if (platform.has("solana")) dao.solana_address = mapper.readTree(platform.toPrettyString()).get("solana").toPrettyString().replace("\"", "");
          if (platform.has("polygon-pos"))
            dao.polygon_address = mapper.readTree(platform.toPrettyString()).get("polygon-pos").toPrettyString().replace("\"", "");
          if (platform.has("xdai")) dao.xdai_address = mapper.readTree(platform.toPrettyString()).get("xdai").toPrettyString().replace("\"", "");
          if (platform.has("tron")) dao.tron_address = mapper.readTree(platform.toPrettyString()).get("tron").toPrettyString().replace("\"", "");
        }
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

  private ObjectNode textNodeToObjectNode(JsonNode node)
  {
    ObjectNode result = mapper.createObjectNode();
    Iterator<JsonNode> i = node.elements();
    while (i.hasNext())
    {
      System.out.println(i.next());
    }
    return result;
  }
}
