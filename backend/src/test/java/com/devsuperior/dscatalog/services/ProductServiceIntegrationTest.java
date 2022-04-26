package com.devsuperior.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.model.repositories.ProductRepository;
import com.devsuperior.dscatalog.model.servicies.ProductService;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTest {

	@Autowired
	private ProductService service;

	@Autowired
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;

	@BeforeEach
	public void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void findAllPagedShouldSortedPageWhenSortByName() {
		
		PageRequest page = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> dto = service.findAllPaged(page);
		
		Assertions.assertFalse(dto.isEmpty());
		Assertions.assertEquals("Macbook Pro", dto.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", dto.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", dto.getContent().get(2).getName());
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPageDoesNotExists() {
		
		PageRequest page = PageRequest.of(50, 10);
		
		Page<ProductDTO> dto = service.findAllPaged(page);
		
		Assertions.assertTrue(dto.isEmpty());
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Zise10() {
		
		PageRequest page = PageRequest.of(0, 10);
		
		Page<ProductDTO> dto = service.findAllPaged(page);
		
		Assertions.assertFalse(dto.isEmpty());
		Assertions.assertEquals(0, dto.getNumber());
		Assertions.assertEquals(10, dto.getSize());
		Assertions.assertEquals(countTotalProduct, dto.getTotalElements());
		Assertions.assertNotNull(dto);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.deleteById(nonExistingId);
		});
		}

	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {

		service.deleteById(existingId);

		Assertions.assertEquals(countTotalProduct - 1, repository.count());

	}

}
