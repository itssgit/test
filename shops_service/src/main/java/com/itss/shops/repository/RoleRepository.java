package com.itss.shops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itss.shops.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	Role findById(Integer id);

}
