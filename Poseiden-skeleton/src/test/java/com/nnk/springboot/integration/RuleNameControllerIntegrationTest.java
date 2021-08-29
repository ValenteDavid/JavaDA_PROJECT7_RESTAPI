package com.nnk.springboot.integration;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.Application;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class RuleNameControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private RuleNameRepository ruleNameRepository;
	
	private int ruleNameId;
	
	
	@BeforeEach
	void init() {
		RuleName ruleName = new RuleName("Rule Name", "Description", "Json", "Template", "SQL", "SQL Part");
		ruleNameId = ruleNameRepository.save(ruleName).getId();
	}

	@AfterEach
	void close() {
		ruleNameRepository.deleteAll();
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void HomeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/ruleName/list"))
				.andExpect(model().attributeExists("ruleNames"))
				.andExpect(view().name("ruleName/list"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "USER")
	public void HomeTest_USER() throws Exception {
		mockMvc.perform(get("/ruleName/list"))
				.andExpect(model().attributeExists("ruleNames"))
				.andExpect(view().name("ruleName/list"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addRuleNameTest_ADMIN() throws Exception {
		mockMvc.perform(get("/ruleName/add"))
				.andExpect(view().name("ruleName/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		mockMvc.perform(post("/ruleName/validate").with(csrf())
				.param("name", "Rule Name")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/ruleName/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/ruleName/update/{0}", ruleNameId))
				.andExpect(model().attributeExists("ruleNameDTOForm"))
				.andExpect(view().name("ruleName/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateRuleName_Test() throws Exception {
		mockMvc.perform(post("/ruleName/update/{0}", ruleNameId).with(csrf())
				.param("name", "RuleName")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/ruleName/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/ruleName/delete/{0}", ruleNameId))
				.andExpect(view().name("redirect:/ruleName/list"))
				.andExpect(status().isFound());

	}

}
