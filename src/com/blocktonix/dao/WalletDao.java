package com.blocktonix.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Wallet")
@Table(name = "wallet_information", schema = "public")
public class WalletDao
{

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private int id;

  @Id
  @Column(name = "wallet_address")
  public String walletAddress = null;

  @Column(name = "wallet_eth_balance")
  public String ethBalance = null;

  @Column(name = "transaction_hash")
  public String transactionHash = null;

  @Column(name = "wallet_balance_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Date walletBalanceAt = null;
}
