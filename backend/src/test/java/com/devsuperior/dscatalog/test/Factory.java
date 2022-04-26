package com.devsuperior.dscatalog.test;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.model.entities.Category;
import com.devsuperior.dscatalog.model.entities.Product;

public class Factory {

	public static Product createProduct() {

		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img/img.png",
				Instant.parse("2020-07-13T20:50:07.12345Z"));
		product.getCategories().add(createCategory());
		return product;
	}

	public static ProductDTO createProductDTO() {

		Product product = createProduct();
		
		return new ProductDTO(product, product.getCategories());
	}
	
	public static Category createCategory() {
		return new Category(2L, "Eletronics");
	}

}
