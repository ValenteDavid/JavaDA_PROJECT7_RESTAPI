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
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class BidControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BidListRepository bidListRepository;
	
	private int bidListId;
	
	@BeforeEach
	void init() {
		BidList bidList = new BidList("Account", "Type", 10.0);
		bidListId = bidListRepository.save(bidList).getBidListId();
	}

	@AfterEach
	void close() {
		bidListRepository.deleteAll();
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/bidList/list"))
				.andExpect(model().attributeExists("bidLists"))
				.andExpect(view().name("bidList/list"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addBidTest_ADMIN() throws Exception {
		mockMvc.perform(get("/bidList/add"))
				.andExpect(view().name("bidList/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		mockMvc.perform(post("/bidList/validate").with(csrf())
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/bidList/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/bidList/update/{0}", bidListId))
				.andExpect(model().attributeExists("bidListDTOForm"))
				.andExpect(view().name("bidList/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateBid_Test() throws Exception {
		mockMvc.perform(post("/bidList/update/{0}", bidListId).with(csrf())
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/bidList/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/bidList/delete/{0}", bidListId))
				.andExpect(view().name("redirect:/bidList/list"))
				.andExpect(status().isFound());
	}

}
