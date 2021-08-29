package com.nnk.springboot.controllers.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.nnk.springboot.domain.RuleName;

public class RuleNameDTOForm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotEmpty(message = "Name is mandatory")
	private String name;
	private String description;
	private String json;

	public RuleNameDTOForm() {
	}

	public RuleNameDTOForm(String name, String description, String json) {
		super();
		this.name = name;
		this.description = description;
		this.json = json;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@Override
	public String toString() {
		return "RuleNameDTOForm [id=" + id + ", name=" + name + ", description=" + description + ", json=" + json + "]";
	}

	public static RuleName DTOToDomain(RuleName ruleName, RuleNameDTOForm ruleNameDTOForm) {
		ruleName.setName(ruleNameDTOForm.getName());
		ruleName.setDescription(ruleNameDTOForm.getDescription());
		ruleName.setJson(ruleNameDTOForm.getJson());
		return ruleName;
	}
	
	public static RuleNameDTOForm DomainToDTO(RuleName ruleName) {
		return new RuleNameDTOForm(ruleName.getName(),ruleName.getDescription(), ruleName.getJson());
	}

}
