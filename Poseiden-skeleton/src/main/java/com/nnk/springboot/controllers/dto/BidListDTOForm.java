package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Range;

import com.nnk.springboot.domain.BidList;

public class BidListDTOForm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer BidListId;
	@NotEmpty(message = "Account is mandatory")
	private String account;
	@NotEmpty(message = "Type is mandatory")
	private String type;
	@Range(min = 0, message = "Bid quantity must be a number greater than or equal to 0")
	private Double bidQuantity;

	public BidListDTOForm() {
	}

	public BidListDTOForm(String account, String type, Double bidQuantity) {
		this.account = account;
		this.type = type;
		this.bidQuantity = bidQuantity;
	}

	public Integer getBidListId() {
		return BidListId;
	}

	public void setBidListId(Integer bidListId) {
		BidListId = bidListId;
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

	public Double getBidQuantity() {
		return bidQuantity;
	}

	public void setBidQuantity(Double bidQuantity) {
		this.bidQuantity = bidQuantity;
	}
	
	public static BidList DTOToDomain(BidList bidList, BidListDTOForm bidListDTOForm) {
		bidList.setAccount(bidListDTOForm.getAccount());
		bidList.setType(bidListDTOForm.getType());
		bidList.setBidQuantity(bidListDTOForm.getBidQuantity());
		return bidList;
	}
	
	public static BidListDTOList DomainToDTO(BidList bidList) {
		return new BidListDTOList(bidList.getBidListId(),bidList.getAccount(), bidList.getType(), bidList.getBidQuantity());
	}

	@Override
	public String toString() {
		return "BidListDTOForm [BidListId=" + BidListId + ", account=" + account + ", type=" + type + ", bidQuantity="
				+ bidQuantity + "]";
	}

}
