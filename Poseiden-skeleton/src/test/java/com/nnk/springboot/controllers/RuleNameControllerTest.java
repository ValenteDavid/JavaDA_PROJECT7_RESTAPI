package com.nnk.springboot.controllers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
public class RuleNameControllerTest {

	@MockBean
	private DataSource dataSource;

	@MockBean
	private RuleNameRepository ruleNameRepository;

	@Autowired
	private MockMvc mockMvc;

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
	@WithMockUser(authorities = "USER")
	public void addRuleNameTest_USER() throws Exception {
		mockMvc.perform(get("/ruleName/add"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		RuleName ruleName = new RuleName("Rule Name", "Description", "Json");
		when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);
		when(ruleNameRepository.findAll()).thenReturn(anyList());

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
	@WithMockUser(authorities = "USER")
	public void validateTest_USER() throws Exception {
		mockMvc.perform(post("/ruleName/validate").with(csrf())
				.param("name", "Rule Name")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validate_EmptyName_Test() throws Exception {
		mockMvc.perform(post("/ruleName/validate").with(csrf())
				.param("name", "")
				.param("description", "Description")
				.param("json", "Json")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("ruleName/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(
				Optional.of(new RuleName("Rule Name", "Description", "Json")));

		mockMvc.perform(get("/ruleName/update/{0}", id))
				.andExpect(model().attributeExists("ruleNameDTOForm"))
				.andExpect(view().name("ruleName/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/ruleName/update/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid ruleName Id:" + id));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateRuleName_Test() throws Exception {
		Integer id = 1;
		RuleName ruleName = new RuleName("RuleName", "Description", "Json");
		ruleName.setId(id);
		when(ruleNameRepository.getById(id)).thenReturn(ruleName);
		when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/ruleName/update/{0}", id).with(csrf())
				.param("id", "1")
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
	public void updateRuleName_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/ruleName/update/{0}", id).with(csrf())
				.param("id", "1")
				.param("curveId", "1")
				.param("term", "1.0")
				.param("value", "AAA")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("ruleName/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(
				Optional.of(new RuleName("Rule Name", "Description", "Json")));
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/ruleName/delete/{0}", id))
				.andExpect(view().name("redirect:/ruleName/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(ruleNameRepository.findById(id)).thenReturn(Optional.empty());
		when(ruleNameRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/ruleName/delete/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid ruleName Id:" + id));

	}

}
