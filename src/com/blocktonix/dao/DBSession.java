package com.blocktonix.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSession
{
  public static final Logger logger = LoggerFactory.getLogger(DBSession.class);

  private static SessionFactory sessionFactory = buildSessionFactory();

  private DBSession()
  {}

  private static SessionFactory buildSessionFactory()
  {
    if (sessionFactory == null)
    {
      Configuration config = new Configuration();
      config.configure();
      logger.info("blockchain db: " + config.getProperty("hibernate.connection.url"));
      logger.info("connect using user: " + config.getProperty("hibernate.connection.username"));
      sessionFactory = config.buildSessionFactory();
    }
    return sessionFactory;
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
