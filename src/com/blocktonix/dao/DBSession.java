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
  {
    // singleton
  }

  private static SessionFactory buildSessionFactory()
  {
    if (sessionFactory == null)
    {
      // StandardServiceRegistry standardRegistry = new
      // StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
      //
      // Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
      Configuration config = new Configuration();
      config.configure();
      System.out.println(config.getProperty("hibernate.connection.url"));
      System.out.println(config.getProperty("hibernate.connection.username"));
      System.out.println(config.getProperty("hibernate.default_schema"));
      sessionFactory = config.buildSessionFactory();
      // sessionFactory = metaData.getSessionFactoryBuilder().build();
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
