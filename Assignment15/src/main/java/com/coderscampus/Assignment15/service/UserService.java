package com.coderscampus.Assignment15.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.dto.ProfileUpdateRequest;
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
		Integer numChildren = request.getNumChildren();
		String childNames = safeTrim(request.getChildNames());
		String childAges = safeTrim(request.getChildAges());

		if (username == null || username.length() < 3) {
			throw new IllegalArgumentException("Username must be at least 3 characters.");
		}
		if (displayName == null || displayName.isBlank()) {
			throw new IllegalArgumentException("Display name is required.");
		}
		if (numChildren == null || numChildren < 1) {
			throw new IllegalArgumentException("Number of children must be 1 or greater.");
		}
		// `childAges` is non-nullable in the entity.
		if (childAges == null || childAges.isBlank()) {
			throw new IllegalArgumentException("Children ages are required.");
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
		user.setNumChildren(numChildren);
		user.setChildNames(childNames);
		user.setChildAges(childAges);

		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		String trimmed = safeTrim(username);
		if (trimmed == null) {
			throw new IllegalArgumentException("Username is required.");
		}
		return userRepository.findByUsernameIgnoreCase(trimmed)
				.orElseThrow(() -> new IllegalStateException("User not found."));
	}

	@Transactional
	public User updateProfile(String currentUsername, ProfileUpdateRequest request) {
		User user = findByUsername(currentUsername);

		String newUsername = safeTrim(request.getUsername());
		String displayName = safeTrim(request.getDisplayName());
		Integer numChildren = request.getNumChildren();
		String childNames = safeTrim(request.getChildNames());
		String childAges = safeTrim(request.getChildAges());

		if (newUsername == null || newUsername.length() < 3) {
			throw new IllegalArgumentException("Username must be at least 3 characters.");
		}
		if (!user.getUsername().equalsIgnoreCase(newUsername)
				&& userRepository.existsByUsernameIgnoreCase(newUsername)) {
			throw new IllegalStateException("That username is already taken.");
		}
		if (displayName == null || displayName.isBlank()) {
			throw new IllegalArgumentException("Display name is required.");
		}
		if (numChildren == null || numChildren < 1) {
			throw new IllegalArgumentException("Number of children must be 1 or greater.");
		}
		if (childAges == null || childAges.isBlank()) {
			throw new IllegalArgumentException("Children ages are required.");
		}

		user.setUsername(newUsername);
		user.setDisplayName(displayName);
		user.setNumChildren(numChildren);
		user.setChildNames(childNames);
		user.setChildAges(childAges);

		return userRepository.save(user);
	}

	@Transactional
	public void updatePassword(String currentUsername, String currentPassword, String newPassword) {
		User user = findByUsername(currentUsername);

		if (currentPassword == null || currentPassword.isBlank()) {
			throw new IllegalArgumentException("Current password is required.");
		}
		if (newPassword == null || newPassword.length() < 8) {
			throw new IllegalArgumentException("New password must be at least 8 characters.");
		}
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect.");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	private static String safeTrim(String value) {
		if (value == null) return null;
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}


