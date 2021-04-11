package com.blocktonix.transaction.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Transaction")
@Table(name = "transaction_information")
public class TransactionDao
{

  @Column(name = "block_hash")
  public String blockHash = null;

  @Column(name = "block_number")
  public String blockNumber = null;

  @Column(name = "contains_contract")
  public String isContract = null;

  @Column(name = "\"from\"")
  public String from = null;

  @Column(name = "gas")
  public String gas = null;

  @Column(name = "gas_price")
  public String gasPrice = null;

  @Id
  @Column(name = "hash")
  public String hash = null;

  @Column(name = "input")
  public String input = null;

  @Column(name = "nonce")
  public String nonce = null;

  @Column(name = "r")
  public String r = null;

  @Column(name = "s")
  public String s = null;

  @Column(name = "transaction_index")
  public String transactionIndex = null;

  @Column(name = "\"to\"")
  public String to = null;

  @Column(name = "v")
  public String v = null;

  @Column(name = "value")
  public String value = null;

}
