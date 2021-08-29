package com.nnk.springboot.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class TradeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TradeRepository tradeRepository;

	private int tradeId;

	@BeforeEach
	void init() {
		Trade trade = new Trade("Account", "Type", 10.0);
		tradeId = tradeRepository.save(trade).getTradeId();
	}

	@AfterEach
	void close() {
		tradeRepository.deleteAll();
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
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
	@WithMockUser(authorities = "ADMIN")
	public void validateTest() throws Exception {
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
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/trade/update/{0}", tradeId))
				.andExpect(model().attributeExists("tradeDTOForm"))
				.andExpect(view().name("trade/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateTrade_Test() throws Exception {
		mockMvc.perform(post("/trade/update/{0}", tradeId).with(csrf())
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
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/trade/delete/{0}", tradeId))
				.andExpect(view().name("redirect:/trade/list"))
				.andExpect(status().isFound());
	}

}
