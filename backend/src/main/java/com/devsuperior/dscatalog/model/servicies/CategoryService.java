package com.devsuperior.dscatalog.model.servicies;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.model.entities.Category;
import com.devsuperior.dscatalog.model.repositories.CategoryRepository;
import com.devsuperior.dscatalog.model.servicies.exception.DatabaseException;
import com.devsuperior.dscatalog.model.servicies.exception.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {

		Page<Category> categories = repository.findAll(pageRequest);

		return categories.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		Category categorie = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

		return new CategoryDTO(categorie);
	}

	@Transactional
	public CategoryDTO save(CategoryDTO dto) {

		Category categorie = new Category();
		categorie.setName(dto.getName());

		categorie = repository.save(categorie);

		return new CategoryDTO(categorie);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		try {
			Category categorie = repository.getOne(id);
			categorie.setName(dto.getName());

			categorie = repository.save(categorie);
			return new CategoryDTO(categorie);
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


}
