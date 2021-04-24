package com.blocktonix.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSession
{
  public static final Logger logger = LoggerFactory.getLogger(DBSession.class);
  private static SessionFactory sessionFactory = null;

  private DBSession()
  {
    // singleton
  }

  public static SessionFactory getSessionFactory()
  {
    if (sessionFactory == null)
    {
      sessionFactory = new Configuration().configure().buildSessionFactory();
    }
    return sessionFactory;
  }
}
