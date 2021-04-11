package com.blocktonix.contract.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Swaps")
@Table(name = "contract_swaps")
public class SwapDao
{

  @Column(name = "tokens_in")
  public String tokensIn = null;

  @Id
  @Column(name = "tokens_out")
  public String tokensOut = null;

  @Column(name = "to_address")
  public String toAddress = null;

}
