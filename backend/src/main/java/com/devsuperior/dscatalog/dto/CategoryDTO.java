package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.model.entities.Category;

public class CategoryDTO {
	
	private Long id;
	private String name;
	
	public CategoryDTO() {

	}

	public CategoryDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CategoryDTO(Category cat) {
		id = cat.getId();
		name = cat.getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
