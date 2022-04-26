package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.model.entities.Category;
import com.devsuperior.dscatalog.model.entities.Product;
import com.devsuperior.dscatalog.model.repositories.CategoryRepository;
import com.devsuperior.dscatalog.model.repositories.ProductRepository;
import com.devsuperior.dscatalog.model.servicies.ProductService;
import com.devsuperior.dscatalog.model.servicies.exception.DatabaseException;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;
import com.devsuperior.dscatalog.test.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	// update deveria (dica: você vai ter que simular o comportamento do getOne)
	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependeteId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;

	@BeforeEach
	public void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependeteId = 4L;

		product = Factory.createProduct();
		
		category = Factory.createCategory();

		page = new PageImpl<>(List.of(product));

		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// update deveria (dica: você vai ter que simular o comportamento do getOne)
		Mockito.when(repository.getOne(existingId)).thenReturn(product);

		// update deveria (dica: você vai ter que simular o comportamento do getOne)
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// update deveria (dica: você vai ter que simular o comportamento do getOne)
		// retornar um ProductDTO quando o id existir
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);

		// update deveria (dica: você vai ter que simular o comportamento do getOne)
		// lançar uma ResourceNotFoundException quando o id não existir
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.doNothing().when(repository).deleteById(existingId);

		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependeteId);

		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		ProductDTO productDTO = Factory.createProductDTO();

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.update(nonExistingId, productDTO);

		});

	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {

		ProductDTO productDTO = Factory.createProductDTO();
		
		ProductDTO dto = service.update(existingId, productDTO);

		Assertions.assertNotNull(dto);
	}

	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.findById(nonExistingId);

		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {

		ProductDTO dto = service.findById(existingId);

		Assertions.assertNotNull(dto);
	}

	@Test
	public void findAllPagedShouldReturnPage() {

		Pageable page = PageRequest.of(0, 10);

		Page<ProductDTO> result = service.findAllPaged(page);

		Assertions.assertNotNull(result);

		Mockito.verify(repository, Mockito.times(1)).findAll(page);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdDependeteId() {

		Assertions.assertThrows(DatabaseException.class, () -> {

			service.deleteById(dependeteId);

		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependeteId);

	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.deleteById(nonExistingId);

		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);

	}

	@Test
	public void deleteShouldDoNottingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {

			service.deleteById(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

	}

}
