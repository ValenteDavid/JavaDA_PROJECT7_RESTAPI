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

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;

@WebMvcTest(controllers = TradeController.class)
public class TradeControllerTest {
	
	@MockBean
	private DataSource dataSource;

	@MockBean
	private TradeRepository tradeRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/trade/list"))
				.andExpect(model().attributeExists("trades"))
				.andExpect(view().name("trade/list"))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = "USER")
	public void homeTest_USER() throws Exception {
		mockMvc.perform(get("/trade/list"))
				.andExpect(model().attributeExists("trades"))
				.andExpect(view().name("trade/list"))
				.andExpect(status().isOk());
	}


	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addTradeTest() throws Exception {
		mockMvc.perform(get("/trade/add"))
				.andExpect(view().name("trade/add"))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = "USER")
	public void addTradeTest_USER() throws Exception {
		mockMvc.perform(get("/trade/add"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest() throws Exception {
		Trade trade = new Trade("Account", "type", 1.0);
		when(tradeRepository.save(trade)).thenReturn(trade);
		when(tradeRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/trade/validate").with(csrf())
				.param("account", "Account")
				.param("type", "type")
				.param("buyQuantity", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/trade/list"))
				.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(authorities = "USER")
	public void validateTest_USER() throws Exception {
		mockMvc.perform(post("/trade/validate").with(csrf())
				.param("account", "Account")
				.param("type", "type")
				.param("buyQuantity", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}
	
	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validate_EmptyAccount_Test() throws Exception {
		mockMvc.perform(post("/trade/validate").with(csrf())
				.param("account", "")
				.param("type", "type")
				.param("buyQuantity", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("trade/add"))
				.andExpect(status().isOk());
	}


	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(tradeRepository.findById(id)).thenReturn(
				Optional.of(new Trade("Account", "type", 1.0)));

		mockMvc.perform(get("/trade/update/{0}", id))
				.andExpect(model().attributeExists("tradeDTOForm"))
				.andExpect(view().name("trade/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(tradeRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/trade/update/{0}", id))
				.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid trade Id:" + id));
	}


	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateTrade_Test() throws Exception {
		Integer id = 1;
		Trade trade = new Trade("Account", "type", 1.0);
		trade.setTradeId(id);
		when(tradeRepository.getById(id)).thenReturn(trade);
		when(tradeRepository.save(trade)).thenReturn(trade);
		when(tradeRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/trade/update/{0}", id).with(csrf())
				.param("account", "account")
				.param("type", "type")
				.param("buyQuantity", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/trade/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateTrade_Test_HasError() throws Exception {
		Integer id = 1;
		mockMvc.perform(post("/trade/update/{0}", id).with(csrf())
				.param("account", "")
				.param("type", "type")
				.param("buyQuantity", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("trade/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(tradeRepository.findById(id)).thenReturn(
				Optional.of(new Trade("Account", "type", 1.0)));
		when(tradeRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/trade/delete/{0}", id))
				.andExpect(view().name("redirect:/trade/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(tradeRepository.findById(id)).thenReturn(Optional.empty());
		when(tradeRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/trade/delete/{0}", id))
						.andExpect(status().isFound()))
				.hasCause(new IllegalArgumentException("Invalid trade Id:" + id));

	}


}
