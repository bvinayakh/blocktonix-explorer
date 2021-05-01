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

  @Column(name = "eth_address")
  public String eth_address = null;

  @Column(name = "bsc_address")
  public String bsc_address = null;

  @Column(name = "polygon_address")
  public String polygon_address = null;

  @Column(name = "solana_address")
  public String solana_address = null;

  @Column(name = "xdai_address")
  public String xdai_address = null;

  @Column(name = "tron_address")
  public String tron_address = null;

}
