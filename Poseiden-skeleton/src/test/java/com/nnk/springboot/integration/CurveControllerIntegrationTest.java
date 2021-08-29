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
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class CurveControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CurvePointRepository curvePointRepository;
	
	private int curvePointId;
	
	@BeforeEach
	void init() {
		CurvePoint curvePoint = new CurvePoint(10, 10d, 30d);
		curvePointId = curvePointRepository.save(curvePoint).getId();
	}

	@AfterEach
	void close() {
		curvePointRepository.deleteAll();
	}
	

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest_ADMIN() throws Exception {
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
	@WithMockUser(authorities = "ADMIN")
	public void validateTest_ADMIN() throws Exception {
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
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/curvePoint/update/{0}", curvePointId))
				.andExpect(model().attributeExists("curvePointDTOForm"))
				.andExpect(view().name("curvePoint/update"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = -1;

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/curvePoint/update/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid curvePoint Id:" + id));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateCurvePoint_Test() throws Exception {
		CurvePoint curvePoint = new CurvePoint(1, 2.0, 1.0);
		curvePoint.setId(curvePointId);

		mockMvc.perform(post("/curvePoint/update/{0}", curvePointId).with(csrf())
				.param("id", ""+curvePointId)
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
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/curvePoint/delete/{0}", curvePointId))
				.andExpect(view().name("redirect:/curvePoint/list"))
				.andExpect(status().isFound());

	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = -1;
		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/curvePoint/delete/{0}", id))
						.andExpect(status().isOk()))
				.hasCause(new IllegalArgumentException("Invalid curvePoint Id:" + id));

	}

}
