package com.coderscampus.Assignment15.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.dto.RegisterRequest;
import com.coderscampus.Assignment15.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User register(RegisterRequest request) {
		String username = safeTrim(request.getUsername());
		String displayName = safeTrim(request.getDisplayName());
		String password = request.getPassword();

		if (username == null || username.length() < 3) {
			throw new IllegalArgumentException("Username must be at least 3 characters.");
		}
		if (displayName == null || displayName.isBlank()) {
			throw new IllegalArgumentException("Display name is required.");
		}
		if (password == null || password.length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters.");
		}
		if (userRepository.existsByUsernameIgnoreCase(username)) {
			throw new IllegalStateException("That username is already taken.");
		}

		User user = new User();
		user.setUsername(username);
		user.setDisplayName(displayName);
		user.setPassword(passwordEncoder.encode(password));

		return userRepository.save(user);
	}

	private static String safeTrim(String value) {
		if (value == null) return null;
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}


