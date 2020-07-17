package it.dstech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.dstech.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
   
	Role findByRole(String role);

}