package com.blocktonix.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSession
{
  public static final Logger logger = LoggerFactory.getLogger(DBSession.class);

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private DBSession()
  {
    // singleton
  }

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

  // public static SessionFactory getSessionFactory()
  // {
  // return sessionFactory;
  // }
  public static Session getSession()
  {
    return sessionFactory.getCurrentSession();
  }

  // public static void shutdown()
  // {
  // getSessionFactory().close();
  // }
}
