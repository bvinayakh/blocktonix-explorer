package com.blocktonix.dao;

import java.io.File;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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

  // public static EntityManager getEntityManager()
  // {
  // if (entitymanager == null)
  // {
  // emfactory = Persistence.createEntityManagerFactory("blocktonix-eth-explorer");
  // logger.debug("Creating new DB Entity Manager Factory " + emfactory.getClass().toString());
  // entitymanager = emfactory.createEntityManager();
  // }
  // else
  // logger.debug("Using existing DB Entity Manager Factory " + emfactory.getClass().toString());
  // return entitymanager;
  //
  // // emfactory = Persistence.createEntityManagerFactory("blocktonix-eth-explorer");
  // // logger.debug("Creating new DB Entity Manager Factory " + emfactory.getClass().toString());
  // // entitymanager = emfactory.createEntityManager();
  // // return entitymanager;
  // }
  //
  // public static void write(Object object)
  // {
  // EntityTransaction dbTx = entitymanager.getTransaction();
  // System.out.println("Using dbtxn: " + dbTx.hashCode());
  // dbTx.begin();
  // entitymanager.persist(object);
  // dbTx.commit();
  // }

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static SessionFactory buildSessionFactory()
  {
    try
    {
      // Create the SessionFactory from hibernate.cfg.xml
      return new Configuration().configure().buildSessionFactory();

    }
    catch (Throwable ex)
    {
      System.err.println("Session Factory Creation Failed " + ex.getMessage());
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory()
  {
    return sessionFactory;
  }

  public static void shutdown()
  {
    getSessionFactory().close();
  }
}
