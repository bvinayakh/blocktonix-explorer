package com.blocktonix.contract.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "ContractABI")
@Table(name = "contract_abi")
public class ContractABIDao
{

  @Column(name = "contract_address")
  public String contractAddress = null;

  @Id
  @Column(name = "contract_symbol")
  public String contractSymbol = null;

  @Column(name = "contract_abi")
  public String contractAbi = null;
}
