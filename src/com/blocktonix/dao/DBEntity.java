package com.blocktonix.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBEntity
{
  public static final Logger logger = LoggerFactory.getLogger(DBEntity.class);

  private static EntityManager entitymanager = null;
  private static EntityManagerFactory emfactory = null;

  private DBEntity()
  {
    // singleton
  }

  public static EntityManager getEntityManager()
  {
    // if (entitymanager == null)
    // {
    // emfactory = Persistence.createEntityManagerFactory("blocktonix-eth-explorer");
    // logger.debug("Creating new DB Entity Manager Factory " + emfactory.getClass().toString());
    // entitymanager = emfactory.createEntityManager();
    // }
    // else
    // logger.debug("Using existing DB Entity Manager Factory " + emfactory.getClass().toString());
    // return entitymanager;

    emfactory = Persistence.createEntityManagerFactory("blocktonix-eth-explorer");
    logger.debug("Creating new DB Entity Manager Factory " + emfactory.getClass().toString());
    entitymanager = emfactory.createEntityManager();
    return entitymanager;
  }
}
