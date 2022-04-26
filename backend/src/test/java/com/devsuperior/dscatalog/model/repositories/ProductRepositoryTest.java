package com.devsuperior.dscatalog.model.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.model.entities.Product;
import com.devsuperior.dscatalog.test.Factory;

@DataJpaTest
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	
	private long nonexistingId;
	
	private long countTotalProducts;
	
	@BeforeEach
	public void setUp() throws Exception {
		existingId = 1L;
		nonexistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	public void findByIdShoulOptionalWhenIdExists() {
		
		Optional<Product> product = repository.findById(existingId);
		Assertions.assertTrue(product.isPresent());
	}
	
	@Test
	public void findByIdShoulOptionalWhenIdNotExists() {
		
		Optional<Product> product = repository.findById(nonexistingId);
		Assertions.assertFalse(product.isPresent());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(existingId);
		
		Optional<Product> product = repository.findById(existingId);
		
		Assertions.assertFalse(product.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessException() {
			
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
			
			repository.deleteById(nonexistingId);
		});
	}

}
