package com.blocktonix.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Tokens")
@Table(name = "tokens_coingecko", schema = "public")
public class TokensDao
{
  @Id
  @Column(name = "coin_id")
  public String coinId = null;

  @Column(name = "coin_symbol")
  public String coinSymbol = null;

  @Column(name = "coin_name")
  public String coinName = null;

  @Column(name = "coin_platforms")
  public String coinPlatforms = null;
}
