package com.devsuperior.dscatalog.resources;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.model.servicies.ProductService;
import com.devsuperior.dscatalog.model.servicies.exception.DatabaseException;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;
import com.devsuperior.dscatalog.test.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResouceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	private ProductDTO dto;
	private PageImpl<ProductDTO> page;
	
	@Autowired
	private ObjectMapper mapper;

	private long existingId;
	private long nonExistingId;
	private long dependeteId;

	@BeforeEach
	public void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependeteId = 4L;

		dto = Factory.createProductDTO();

		page = new PageImpl<>(List.of(dto));

		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

		when(service.findById(existingId)).thenReturn(dto);

		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(service.update(ArgumentMatchers.eq( existingId ), ArgumentMatchers.any())).thenReturn(dto);

		when(service.update(ArgumentMatchers.eq( nonExistingId ), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class); 
		
		when(service.save(ArgumentMatchers.any())).thenReturn(dto);
		
		doNothing().when(service).deleteById(existingId);
		
		doThrow(ResourceNotFoundException.class).when(service).deleteById(nonExistingId);
		
		doThrow(DatabaseException.class).when(service).deleteById(dependeteId);
		
	}
	
	@Test
	public void deleteByIdReturnNotFoundtWhenIdDoesNotExists() throws Exception {
		
		ResultActions actions = mockMvc.perform(delete("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteByIdReturnNoContentWhenIdExists() throws Exception {
		
		ResultActions actions = mockMvc.perform(delete("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isNoContent());
	}
	
	@Test
	public void saveReturnProductDTOCreated() throws Exception {
		
		String jsonBody = mapper.writeValueAsString(dto);

		ResultActions actions = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isCreated());
		actions.andExpect(jsonPath("$.id").exists());
		actions.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void updateReturnProductDTOWhenIdDoesNotExists() throws Exception {

		String jsonBody = mapper.writeValueAsString(dto);

		ResultActions actions = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isNotFound());
	}

	@Test
	public void updateReturnProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = mapper.writeValueAsString(dto);

		ResultActions actions = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.id").exists());
		actions.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void findAllSouldReturnPage() throws Exception {

		mockMvc.perform(get("/products")).andExpect(status().isOk());
	}

	@Test
	public void findByIdReturnProductWhenIdExists() throws Exception {

		ResultActions actions = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.id").exists());
		actions.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void findByIdReturnNotFoundWhenDoesNotExists() throws Exception {

		ResultActions actions = mockMvc
				.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().isNotFound());
	}
}
