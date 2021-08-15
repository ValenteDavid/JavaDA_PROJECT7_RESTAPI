package com.nnk.springboot.controllers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;

@WebMvcTest(controllers = RatingController.class)
public class RatingControllerTest {

	@MockBean
	private RatingRepository ratingRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void HomeTest() throws Exception {
		mockMvc.perform(get("/rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(view().name("rating/list"));
	}

	@Test
	public void addRatingTest() throws Exception {
		mockMvc.perform(get("/rating/add"))
				.andExpect(view().name("rating/add"));
	}

	@Test
	public void validateTest() throws Exception {
		Rating rating = new Rating("Moodys Rating", "SandPRating", "Fitch Rating");
		when(ratingRepository.save(rating)).thenReturn(rating);
		when(ratingRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/rating/validate")
				.param("moodysRating", "Moodys Rating")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/rating/list"));
	}
	
	@Test
	public void validate_EmptyModdysRating_Test() throws Exception {
		mockMvc.perform(post("/rating/validate")
				.param("moodysRating", "")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("rating/add"));
	}

	@Test
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(ratingRepository.findById(id)).thenReturn(
				Optional.of(new Rating("Moodys Rating", "Sand PRating", "Fitch Rating")));

		mockMvc.perform(get("/rating/update/{0}", id))
				.andExpect(model().attributeExists("rating"))
				.andExpect(view().name("rating/update"));
	}

	@Test
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(ratingRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/rating/update/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid rating Id:" + id));
	}


	@Test
	public void updateRating_Test() throws Exception {
		Integer id = 1;
		Rating rating = new Rating("Moodys Rating", "SandPRating", "Fitch Rating");
		rating.setId(id);
		when(ratingRepository.save(rating)).thenReturn(rating);
		when(ratingRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/rating/update/{id}", id)
				.param("id", "1")
				.param("moodysRating", "Moodys Rating")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/rating/list"));
	}
	
	@Test
	public void updateRating_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/rating/update/{id}", id)
				.param("id", "1")
				.param("moodysRating", "")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("rating/update"));
	}

	@Test
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(ratingRepository.findById(id)).thenReturn(
				Optional.of(new Rating("Moodys Rating", "SandPRating", "Fitch Rating")));
		when(ratingRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/rating/delete/{0}", id))
				.andExpect(view().name("redirect:/rating/list"));

	}

	@Test
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(ratingRepository.findById(id)).thenReturn(Optional.empty());
		when(ratingRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/rating/delete/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid rating Id:" + id));

	}

	
}
