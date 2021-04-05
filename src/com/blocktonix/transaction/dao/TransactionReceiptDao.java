package com.blocktonix.transaction.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "TransactionReceipt")
@Table(name = "transaction_receipt")
public class TransactionReceiptDao
{

  @Column(name = "block_hash")
  public String blockHash = null;

  @Column(name = "block_number")
  public String blockNumber = null;

  @Column(name = "contract_address")
  public String contractAddress = null;

  @Column(name = "cumulative_gas_used")
  public String cumulativeGasUsed = null;

  @Column(name = "\"from\"")
  public String from = null;

  @Column(name = "gas_used")
  public String gasUsed = null;

  @Column(name = "logs")
  public String logs = null;

  @Column(name = "logs_bloom")
  public String logsBloom = null;

  @Column(name = "status")
  public String status = null;

  @Column(name = "\"to\"")
  public String to = null;

  @Id
  @Column(name = "transaction_hash")
  public String transactionHash = null;

  @Column(name = "transaction_index")
  public String transactionIndex = null;

  @Column(name = "transaction_status")
  public String transactionStatus = null;

  @Column(name = "logs_address")
  public String logsAddress = null;

  @Column(name = "logs_data")
  public String logsData = null;

  @Column(name = "logs_log_index")
  public String logsLogIndex = null;

  @Column(name = "logs_topics")
  public String logsTopics = null;

  @Column(name = "logs_type")
  public String logsType = null;

}
