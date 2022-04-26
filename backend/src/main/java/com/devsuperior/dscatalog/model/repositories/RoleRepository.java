package com.devsuperior.dscatalog.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.model.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
