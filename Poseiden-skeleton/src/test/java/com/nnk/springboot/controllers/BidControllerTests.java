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

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;

@WebMvcTest(controllers = BidListController.class)
public class BidControllerTests {

	@MockBean
	private DataSource dataSource;

	@MockBean
	private BidListRepository bidListRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/bidList/list"))
				.andExpect(model().attributeExists("bidLists"))
				.andExpect(view().name("bidList/list"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "USER")
	public void homeTest_USER() throws Exception {
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
	@WithMockUser(authorities = "USER")
	public void addBidTest_USER() throws Exception {
		mockMvc.perform(get("/bidList/add"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		BidList bidList = new BidList("Account", "Type", 10.0);
		when(bidListRepository.save(bidList)).thenReturn(bidList);
		when(bidListRepository.findAll()).thenReturn(anyList());

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
	@WithMockUser(authorities = "USER")
	public void validateTest_USER() throws Exception {
		mockMvc.perform(post("/bidList/validate").with(csrf())
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validate_EmptyAccount_Test() throws Exception {
		mockMvc.perform(post("/bidList/validate").with(csrf())
				.param("account", "")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/add"))
				.andExpect(status().isOk());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validate_EmptyType_Test() throws Exception {
		mockMvc.perform(post("/bidList/validate").with(csrf())
				.param("account", "Account")
				.param("type", "")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(
				Optional.of(new BidList("Account", "Type", 10.0)));

		mockMvc.perform(get("/bidList/update/{0}", id))
				.andExpect(model().attributeExists("bidListDTOForm"))
				.andExpect(view().name("bidList/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/bidList/update/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid bidList Id:" + id));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateBid_Test() throws Exception {
		Integer id = 1;
		BidList bidList = new BidList("Account", "Type", 10.0);
		bidList.setBidListId(id);
		when(bidListRepository.getById(id)).thenReturn(bidList);
		when(bidListRepository.save(bidList)).thenReturn(bidList);
		when(bidListRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/bidList/update/{0}", id).with(csrf())
				.param("id", "1")
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
	public void updateBid_Test_HasError() throws Exception {
		Integer id = 1;
		mockMvc.perform(post("/bidList/update/{0}", id).with(csrf())
				.param("id", "1")
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "AAA")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(
				Optional.of(new BidList("Account", "Type", 10.0)));
		when(bidListRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/bidList/delete/{0}", id))
				.andExpect(view().name("redirect:/bidList/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(Optional.empty());
		when(bidListRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/bidList/delete/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid bidList Id:" + id));

	}

}
