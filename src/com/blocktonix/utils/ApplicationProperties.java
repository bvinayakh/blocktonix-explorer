package com.blocktonix.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties
{
  private String propertiesFileName = System.getProperty("user.home") + File.separator + "blocktonix" + File.separator + "blocktonix.properties";
  private Properties prop = new Properties();

  public ApplicationProperties()
  {
    try
    {
      InputStream inputStream = new FileInputStream(new File(propertiesFileName));
      prop.load(inputStream);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  public String getProperty(String key)
  {
    return prop.getProperty(key);
  }
}
