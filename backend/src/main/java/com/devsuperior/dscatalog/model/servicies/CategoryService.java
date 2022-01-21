package com.devsuperior.dscatalog.model.servicies;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.model.entities.Category;
import com.devsuperior.dscatalog.model.repositories.CategoryRepository;
import com.devsuperior.dscatalog.model.servicies.exception.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		
		List<Category> categories = repository.findAll();
		
		return categories.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		
		Category categorie = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		
		return new CategoryDTO(categorie);
	}

	@Transactional
	public CategoryDTO save(CategoryDTO dto) {

		Category categorie = new Category();
		categorie.setName(dto.getName());
		
		categorie = repository.save(categorie);
		
		return new CategoryDTO(categorie);
	}
}
