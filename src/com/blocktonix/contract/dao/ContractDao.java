package com.blocktonix.contract.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Contract")
@Table(name = "contract_information")
public class ContractDao
{

  @Column(name = "contract_block_number")
  public String blockNumber = null;

  @Id
  @Column(name = "contract_txn_hash")
  public String transactionHash = null;

  @Column(name = "contract_address")
  public String address = null;

  @Column(name = "contract_symbol")
  public String symbol = null;

  @Column(name = "contract_name")
  public String name = null;

  @Column(name = "contract_initiated_from")
  public String from = null;

  @Column(name = "contract_sent_to")
  public String to = null;

  @Column(name = "contract_amount")
  public String amount = null;

  @Column(name = "contract_decimals")
  public String decimals = null;

  @Column(name = "contract_total_supply")
  public String totalSupply = null;

  @Column(name = "contract_abi")
  public String abi = null;

}
