package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.nnk.springboot.domain.CurvePoint;

public class CurvePointDTOForm {
  
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotNull(message = "Curve id is mandatory")
	private Integer curveId;
	private Double term;
	private Double value;
	
	public CurvePointDTOForm() {
	}
	
	public CurvePointDTOForm(Integer id, Integer curveId, Double term,
			Double value) {
		super();
		this.id = id;
		this.curveId = curveId;
		this.term = term;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCurveId() {
		return curveId;
	}
	public void setCurveId(Integer curveId) {
		this.curveId = curveId;
	}
	public Double getTerm() {
		return term;
	}
	public void setTerm(Double term) {
		this.term = term;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	public static CurvePoint DTOToDomain(CurvePoint curvePoint, CurvePointDTOForm curvePointDTOForm) {
		curvePoint.setCurveId(curvePointDTOForm.getCurveId());
		curvePoint.setTerm(curvePointDTOForm.getTerm());
		curvePoint.setValue(curvePointDTOForm.getValue());
		return curvePoint;
	}
	
	public static CurvePointDTOForm DomaintoDTO(CurvePoint curvePoint) {
		return new CurvePointDTOForm(curvePoint.getId(), curvePoint.getCurveId(), curvePoint.getTerm(), curvePoint.getValue());
	}

	@Override
	public String toString() {
		return "CurvePointDTOForm [id=" + id + ", curveId=" + curveId + ", term=" + term + ", value=" + value + "]";
	}

}
