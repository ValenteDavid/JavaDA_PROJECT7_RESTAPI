package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.nnk.springboot.domain.Trade;

public class TradeDTOForm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer tradeId;
	@NotEmpty(message = "Account is mandatory")
	private String account;
	private String type;
	private Double buyQuantity;

	public TradeDTOForm() {
	}

	public TradeDTOForm(String account, String type) {
		this.account = account;
		this.type = type;
	}

	public TradeDTOForm(String account, String type, Double buyQuantity) {
		super();
		this.account = account;
		this.type = type;
		this.buyQuantity = buyQuantity;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getBuyQuantity() {
		return buyQuantity;
	}

	public void setBuyQuantity(Double buyQuantity) {
		this.buyQuantity = buyQuantity;
	}

	public static Trade DTOToDomain(Trade trade, TradeDTOForm tradeDTOForm) {
		trade.setAccount(tradeDTOForm.getAccount());
		trade.setType(tradeDTOForm.getType());
		trade.setBuyQuantity(tradeDTOForm.getBuyQuantity());
		return trade;
	}

	public static TradeDTOForm DomainToDTO(Trade trade) {
		return new TradeDTOForm(trade.getAccount(), trade.getType(), trade.getBuyQuantity());
	}

	@Override
	public String toString() {
		return "TradeDTOForm [tradeId=" + tradeId + ", account=" + account + ", type=" + type + ", buyQuantity="
				+ buyQuantity + "]";
	}

}
