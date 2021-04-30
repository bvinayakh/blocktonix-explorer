package com.blocktonix.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Block")
@Table(name = "block_information", schema = "public")
public class BlockDao
{

  @Column(name = "difficulty")
  public String difficulty = null;

  @Column(name = "extra_data")
  public String extraData = null;

  @Column(name = "gas_limit")
  public String gasLimit = null;

  @Column(name = "gas_used")
  public String gasUsed = null;

  @Column(name = "hash")
  public String hash = null;

  @Column(name = "logs_bloom")
  public String logsBloom = null;

  @Column(name = "miner")
  public String miner = null;

  @Column(name = "mix_hash")
  public String mixHash = null;

  @Column(name = "nonce")
  public String nonce = null;

  @Id
  @Column(name = "number")
  public String number = null;

  @Column(name = "parent_hash")
  public String parentHash = null;

  @Column(name = "receipts_root")
  public String receiptsRoot = null;

  @Column(name = "sha3_uncles")
  public String sha3Uncles = null;

  @Column(name = "size")
  public String size = null;

  @Column(name = "state_root")
  public String stateRoot = null;

  @Column(name = "timestamp")
  public String timestamp = null;

  @Column(name = "total_difficulty")
  public String totalDifficulty = null;

  @Column(name = "transactions")
  public String transactions = null;

  @Column(name = "transactions_root")
  public String transactionsRoot = null;

  @Column(name = "uncles")
  public String uncles = null;

}
