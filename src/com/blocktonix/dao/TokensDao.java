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
  public String coin_id = null;

  @Column(name = "coin_symbol")
  public String coin_symbol = null;

  @Column(name = "coin_name")
  public String coin_name = null;

  @Column(name = "coin_platforms")
  public String coin_platforms = null;
}
