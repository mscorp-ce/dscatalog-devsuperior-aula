package com.devsuperior.dscatalog.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.model.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
