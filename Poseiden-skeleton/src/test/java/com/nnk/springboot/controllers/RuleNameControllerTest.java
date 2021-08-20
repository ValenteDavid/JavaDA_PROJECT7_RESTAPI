package com.nnk.springboot.controllers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;

@WebMvcTest(controllers = RuleNameController.class)
@WithMockUser(roles = "USER")
public class RuleNameControllerTest {
	
	@MockBean
	private DataSource dataSource;
	
	@MockBean
	private RuleNameRepository ruleNameRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void HomeTest() throws Exception {
		mockMvc.perform(get("/ruleName/list"))
				.andExpect(model().attributeExists("ruleNames"))
				.andExpect(view().name("ruleName/list"));
	}

	@Test
	public void addRuleNameTest() throws Exception {
		mockMvc.perform(get("/ruleName/add"))
				.andExpect(view().name("ruleName/add"));
	}

	@Test
	public void validateTest() throws Exception {
		RuleName ruleName = new RuleName("Rule Name", "Description", "Json");
		when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/ruleName/validate").with(csrf())
				.param("name", "Rule Name")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/ruleName/list"));
	}
	
	@Test
	public void validate_EmptyName_Test() throws Exception {
		mockMvc.perform(post("/ruleName/validate").with(csrf())
				.param("name", "")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("ruleName/add"));
	}


	@Test
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(
				Optional.of(new RuleName("Rule Name", "Description", "Json")));

		mockMvc.perform(get("/ruleName/update/{0}", id))
				.andExpect(model().attributeExists("ruleName"))
				.andExpect(view().name("ruleName/update"));
	}

	@Test
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/ruleName/update/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid ruleName Id:" + id));
	}


	@Test
	public void updateRuleName_Test() throws Exception {
		Integer id = 1;
		RuleName ruleName = new RuleName("RuleName", "Description", "Json");
		ruleName.setId(id);
		when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/ruleName/update/{id}", id).with(csrf())
				.param("id", "1")
				.param("name", "RuleName")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/ruleName/list"));
	}

	@Test
	public void updateRuleName_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/ruleName/update/{id}", id).with(csrf())
				.param("id", "1")
				.param("curveId", "1")
				.param("term", "1.0")
				.param("value", "AAA")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("ruleName/update"));
	}

	@Test
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(
				Optional.of(new RuleName("Rule Name", "Description", "Json")));
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/ruleName/delete/{0}", id))
				.andExpect(view().name("redirect:/ruleName/list"));

	}

	@Test
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(Optional.empty());
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/ruleName/delete/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid ruleName Id:" + id));

	}


}
