package com.blocktonix.wallet.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blocktonix.contract.dao.ContractABIDao;
import com.blocktonix.contract.dao.ContractDao;
import com.blocktonix.dao.DBEntity;

public class WalletDBOperations
{
  public static final Logger logger = LoggerFactory.getLogger(WalletDBOperations.class);

  private Session session = null;

  public WalletDBOperations()
  {
    session = DBEntity.getSessionFactory().openSession();
  }

  public void getWalletInformation()
  {
    EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ContractDao> criteriaQuery = criteriaBuilder.createQuery(ContractDao.class);
    Root<ContractDao> from = criteriaQuery.from(ContractDao.class);
    criteriaQuery.orderBy(criteriaBuilder.desc(from.get("contract_block_number")));
    TypedQuery<ContractDao> query = entityManager.createQuery(criteriaQuery);
    List<ContractDao> blocksList = query.getResultList();
    entityManager.close();
  }

  public void storeContractAbi(String contractAddress, String contractSymbol, String contractAbi)
  {
    ContractABIDao dao = new ContractABIDao();
    dao.contractAbi = contractAbi;
    dao.contractAddress = contractAddress;
    dao.contractSymbol = contractSymbol;

    session.beginTransaction();
    session.save(dao);
    session.getTransaction().commit();
    logger.info("stored contract ABI for " + contractSymbol);
  }
}
