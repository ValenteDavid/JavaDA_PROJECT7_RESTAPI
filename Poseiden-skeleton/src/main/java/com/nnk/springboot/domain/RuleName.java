package com.nnk.springboot.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "rulename")
public class RuleName {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@NotEmpty(message = "Name is mandatory")
	private String name;
	private String description;
	private String json;
	private String template;
	private String sqlStr;
	private String sqlPart;
	
	public RuleName() {
	}
	
	public RuleName(String name, String description, String json) {
		super();
		this.name = name;
		this.description = description;
		this.json = json;
	}

	public RuleName(String name, String description, String json,
			String template, String sqlStr, String sqlPart) {
		this.name = name;
		this.description = description;
		this.json = json;
		this.template = template;
		this.sqlStr = sqlStr;
		this.sqlPart = sqlPart;
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
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getSqlStr() {
		return sqlStr;
	}
	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}
	public String getSqlPart() {
		return sqlPart;
	}
	public void setSqlPart(String sqlPart) {
		this.sqlPart = sqlPart;
	}

	@Override
	public String toString() {
		return "RuleName [id=" + id + ", name=" + name + ", description=" + description + ", json=" + json
				+ ", template=" + template + ", sqlStr=" + sqlStr + ", sqlPart=" + sqlPart + "]";
	}
	
}
