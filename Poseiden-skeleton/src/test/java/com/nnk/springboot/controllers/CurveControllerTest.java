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

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;

@WebMvcTest(controllers = CurveController.class)
public class CurveControllerTest {

	@MockBean
	private DataSource dataSource;

	@MockBean
	private CurvePointRepository curvePointRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
		mockMvc.perform(get("/curvePoint/list"))
				.andExpect(model().attributeExists("curvePoints"))
				.andExpect(view().name("curvePoint/list"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "USER")
	public void HomeTest_USER() throws Exception {
		mockMvc.perform(get("/curvePoint/list"))
				.andExpect(model().attributeExists("curvePoints"))
				.andExpect(view().name("curvePoint/list"))
				.andExpect(status().isOk());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addCurvePointTest_ADMIN() throws Exception {
		mockMvc.perform(get("/curvePoint/add"))
				.andExpect(view().name("curvePoint/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "USER")
	public void addCurvePointTest_USER() throws Exception {
		mockMvc.perform(get("/curvePoint/add"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
		CurvePoint curvePoint = new CurvePoint(1, 1.0, 1.0);
		when(curvePointRepository.save(curvePoint)).thenReturn(curvePoint);
		when(curvePointRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/curvePoint/validate").with(csrf())
				.param("curveId", "1")
				.param("term", "1.0")
				.param("value", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/curvePoint/list"))
				.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(authorities = "USER")
	public void validateTest_USER() throws Exception {
		mockMvc.perform(post("/curvePoint/validate").with(csrf())
				.param("curveId", "1")
				.param("term", "1.0")
				.param("value", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void validate_EmptyCurveId_Test() throws Exception {
		mockMvc.perform(post("/curvePoint/validate").with(csrf())
				.param("curveId", "")
				.param("term", "1.0")
				.param("value", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("curvePoint/add"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(curvePointRepository.findById(id)).thenReturn(
				Optional.of(new CurvePoint(1, 1.0, 1.0)));

		mockMvc.perform(get("/curvePoint/update/{0}", id))
				.andExpect(model().attributeExists("curvePointDTOForm"))
				.andExpect(view().name("curvePoint/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(curvePointRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/curvePoint/update/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid curvePoint Id:" + id));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateCurvePoint_Test() throws Exception {
		Integer id = 1;
		CurvePoint curvePoint = new CurvePoint(1, 2.0, 1.0);
		curvePoint.setId(id);
		when(curvePointRepository.getById(id)).thenReturn(curvePoint);
		when(curvePointRepository.save(curvePoint)).thenReturn(curvePoint);
		when(curvePointRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(post("/curvePoint/update/{0}", id).with(csrf())
				.param("id", "1")
				.param("curveId", "1")
				.param("term", "2.0")
				.param("value", "1.0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/curvePoint/list"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateCurvePoint_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/curvePoint/update/{0}", id).with(csrf())
				.param("id", "1")
				.param("curveId", "1")
				.param("term", "1.0")
				.param("value", "AAA")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("curvePoint/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(curvePointRepository.findById(id)).thenReturn(
				Optional.of(new CurvePoint(1, 1.0, 10.0)));
		when(curvePointRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/curvePoint/delete/{0}", id))
				.andExpect(view().name("redirect:/curvePoint/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(curvePointRepository.findById(id)).thenReturn(Optional.empty());
		when(curvePointRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/curvePoint/delete/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid curvePoint Id:" + id));

	}

}
