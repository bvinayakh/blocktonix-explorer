package com.blocktonix.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "ContractETHValue")
@Table(name = "contract_eth_value", schema = "public")
public class ContractEthValueDao
{

  @Column(name = "contract_symbol")
  public String contractSymbol = null;

  @Column(name = "contract_address")
  public String contractAddress = null;

  @Id
  @Column(name = "contract_txn")
  public String contractTransactionHash = null;

  @Column(name = "contract_amount")
  public String contractAmount = null;

  @Column(name = "contract_value_eth")
  public String contractValueEth = null;

  @Column(name = "contract_value_usd")
  public String contractValueUsd = null;

  @Column(name = "contract_block_number")
  public String contractBlockNumber = null;

  @Column(name = "contract_block_date")
  public String contractBlockDate = null;

  @Column(name = "contract_block_month")
  public String contractBlockMonth = null;

  @Column(name = "contract_block_year")
  public String contractBlockYear = null;

  @Column(name = "contract_block_hour")
  public String contractBlockHour = null;

  @Column(name = "contract_block_minute")
  public String contractBlockMinute = null;

  @Column(name = "contract_block_second")
  public String contractBlockSecond = null;
}
