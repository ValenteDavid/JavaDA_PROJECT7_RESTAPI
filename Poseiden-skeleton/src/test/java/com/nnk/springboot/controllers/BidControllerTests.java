package com.nnk.springboot.controllers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;

@WebMvcTest(controllers = BidListController.class)
public class BidControllerTests {

	@MockBean
	private BidListRepository bidListRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void homeTest() throws Exception {
		mockMvc.perform(get("/bidList/list"))
				.andExpect(model().attributeExists("bidLists"))
				.andExpect(view().name("bidList/list"));
	}

	@Test
	public void addBidTest() throws Exception {
		mockMvc.perform(get("/bidList/add"))
				.andExpect(view().name("bidList/add"));
	}

	@Test
	public void validateTest() throws Exception {
		BidList bidList = new BidList("Account", "Type", 10.0);
		when(bidListRepository.save(bidList)).thenReturn(bidList);
		when(bidListRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/bidList/validate")
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/bidList/list"));
	}

	@Test
	public void validate_EmptyAccount_Test() throws Exception {
		mockMvc.perform(post("/bidList/validate")
				.param("account", "")
				.param("type", "Type")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/add"));
	}

	@Test
	public void validate_EmptyType_Test() throws Exception {
		mockMvc.perform(post("/bidList/validate")
				.param("account", "Account")
				.param("type", "")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/add"));
	}

	@Test
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(
				Optional.of(new BidList("Account", "Type", 10.0)));

		mockMvc.perform(get("/bidList/update/{0}", id))
				.andExpect(model().attributeExists("bidList"))
				.andExpect(view().name("bidList/update"));
	}

	@Test
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/bidList/update/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid bidList Id:" + id));
	}

	@Test
	public void updateBid_Test() throws Exception {
		Integer id = 1;
		BidList bidList = new BidList("Account", "Type", 10.0);
		bidList.setBidListId(id);
		when(bidListRepository.save(bidList)).thenReturn(bidList);
		when(bidListRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/bidList/update/{id}", id)
				.param("id", "1")
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "10.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/bidList/list"));
	}

	@Test
	public void updateBid_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/bidList/update/{id}", id)
				.param("id", "1")
				.param("account", "Account")
				.param("type", "Type")
				.param("bidQuantity", "AAA")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("bidList/update"));
	}

	@Test
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(
				Optional.of(new BidList("Account", "Type", 10.0)));
		when(bidListRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/bidList/delete/{0}", id))
				.andExpect(view().name("redirect:/bidList/list"));

	}

	@Test
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(bidListRepository.findById(id)).thenReturn(Optional.empty());
		when(bidListRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/bidList/delete/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid bidList Id:" + id));

	}

}
