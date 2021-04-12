package com.blocktonix.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties
{
  static private Logger log = LoggerFactory.getLogger(ApplicationProperties.class);
  static private String propertiesFileName = System.getProperty("user.home") + File.separator + "explorer.properties";
  static private Properties prop = new Properties();
  static
  {
    log.info("Loading properties from ::: " + System.getProperty("user.home"));
    try
    {
      InputStream inputStream = new FileInputStream(new File(propertiesFileName));
      prop.load(inputStream);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      log.info("Error reading properties. " + ioe.getMessage());
    }
  }

  public static String getProperties(String key)
  {
    return prop.getProperty(key);
  }
}
