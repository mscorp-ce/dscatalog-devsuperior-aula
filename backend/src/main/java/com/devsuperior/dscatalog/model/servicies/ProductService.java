package com.devsuperior.dscatalog.model.servicies;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.model.entities.Category;
import com.devsuperior.dscatalog.model.entities.Product;
import com.devsuperior.dscatalog.model.repositories.CategoryRepository;
import com.devsuperior.dscatalog.model.repositories.ProductRepository;
import com.devsuperior.dscatalog.model.servicies.exception.DatabaseException;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable page) {

		Page<Product> products = repository.findAll(page);

		return products.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO save(ProductDTO dto) {

		Product product = new Product();
		copyDtoToEntity(dto, product);

		product = repository.save(product);

		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {

		try {
			Product product = repository.getOne(id);
			copyDtoToEntity(dto, product);

			product = repository.save(product);
			return new ProductDTO(product);
		}
		catch(EntityNotFoundException e){
			throw new ResourceNotFoundException("Id not found "+ id);
		}
	}
	
	public void deleteById(Long id) {

		try {
			repository.deleteById(id);
			
		}
		catch(EmptyResultDataAccessException e){
			throw new ResourceNotFoundException("Id not found "+ id);
		}
		catch(DataIntegrityViolationException e){
			throw new DatabaseException("Integrity Violation");
		}
	}

	private void copyDtoToEntity(ProductDTO dto, Product product) {
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPreice(dto.getPrice());
		product.setDate(dto.getDate());
		product.setImgUrl(dto.getImgUrl());
		
		product.getCategories().clear();
		
		for(CategoryDTO catDTO : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDTO.getId());
			
			product.getCategories().add(category);
		}

	}

}
