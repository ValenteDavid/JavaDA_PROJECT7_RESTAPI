package com.nnk.springboot.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class RatingControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RatingRepository ratingRepository;
	
	private int ratingId;
	
	@BeforeEach
	void init() {
		Rating rating = new Rating("Moodys Rating", "Sand PRating", "Fitch Rating", 10);
		ratingId = ratingRepository.save(rating).getId();
	}

	@AfterEach
	void close() {
		ratingRepository.deleteAll();
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(view().name("rating/list"))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addRatingTest_ADMIN() throws Exception {
		mockMvc.perform(get("/rating/add"))
				.andExpect(view().name("rating/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		mockMvc.perform(post("/rating/validate").with(csrf())
				.param("moodysRating", "Moodys Rating")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/rating/list"))
				.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/rating/update/{0}", ratingId))
				.andExpect(model().attributeExists("ratingDTOForm"))
				.andExpect(view().name("rating/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = -1;
		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/rating/update/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid rating Id:" + id));
	}


	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateRating_Test() throws Exception {
		Rating rating = new Rating("Moodys Rating", "SandPRating", "Fitch Rating");
		rating.setId(ratingId);

		mockMvc.perform(post("/rating/update/{0}", ratingId).with(csrf())
				.param("id", ""+ratingId)
				.param("moodysRating", "Moodys Rating")
				.param("sandPRating", "SandPRating")
				.param("fitchRating", "Fitch Rating")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/rating/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/rating/delete/{0}", ratingId))
				.andExpect(view().name("redirect:/rating/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = -1;
		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/rating/delete/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid rating Id:" + id));

	}

	
}
