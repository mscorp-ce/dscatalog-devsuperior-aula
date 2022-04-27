package com.devsuperior.dscatalog.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.model.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}