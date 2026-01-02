package com.coderscampus.Assignment15.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coderscampus.Assignment15.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsernameIgnoreCase(String username);
	boolean existsByUsernameIgnoreCase(String username);
}


