package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.test.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;

	private long existingId;

	private long nonExistingId;

	private long countTotalProducts;

	@BeforeEach
	public void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void updateReturnProductDTOWhenIdExists() throws Exception {
		
		ProductDTO dto = Factory.createProductDTO();
		
		String jsonBody = mapper.writeValueAsString(dto);
		
		String expectedName = dto.getName();
		
		String expectedDescription = dto.getDescription();

		ResultActions actions = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.id").exists());
		actions.andExpect(jsonPath("$.id").value(existingId));
		actions.andExpect(jsonPath("$.name").value(expectedName));
		actions.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		ProductDTO dto = Factory.createProductDTO();
		
		String jsonBody = mapper.writeValueAsString(dto);

		ResultActions actions = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isNotFound());

	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		ResultActions actions = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON));
		
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		actions.andExpect(jsonPath("$.content").exists());
		actions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		actions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		actions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

}
