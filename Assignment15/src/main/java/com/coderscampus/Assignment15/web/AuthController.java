package com.coderscampus.Assignment15.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.dto.ProfileResponse;
import com.coderscampus.Assignment15.dto.ProfileUpdateRequest;
import com.coderscampus.Assignment15.dto.PasswordUpdateRequest;
import com.coderscampus.Assignment15.dto.RegisterRequest;
import com.coderscampus.Assignment15.dto.UserResponse;
import com.coderscampus.Assignment15.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		try {
			User created = userService.register(request);
			return new ResponseEntity<>(
					new UserResponse(created.getUserId(), created.getUsername(), created.getDisplayName()),
					HttpStatus.CREATED);
		} catch (IllegalArgumentException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalStateException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return new ResponseEntity<>("Not authenticated.", HttpStatus.UNAUTHORIZED);
		}

		try {
			User user = userService.findByUsername(authentication.getName());
			return new ResponseEntity<>(
					new ProfileResponse(
							user.getUserId(),
							user.getUsername(),
							user.getDisplayName(),
							user.getNumChildren(),
							user.getChildNames(),
							user.getChildAges()
					),
					HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalStateException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/me")
	public ResponseEntity<?> updateMe(@RequestBody ProfileUpdateRequest request, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return new ResponseEntity<>("Not authenticated.", HttpStatus.UNAUTHORIZED);
		}

		try {
			String currentUsername = authentication.getName();
			User updated = userService.updateProfile(currentUsername, request);

			// If username changed, update the session's Authentication so future requests keep working.
			if (!currentUsername.equalsIgnoreCase(updated.getUsername())) {
				UserDetails newPrincipal = new org.springframework.security.core.userdetails.User(
						updated.getUsername(),
						updated.getPassword(),
						authentication.getAuthorities()
				);
				UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
						newPrincipal,
						authentication.getCredentials(),
						authentication.getAuthorities()
				);
				newAuth.setDetails(authentication.getDetails());
				SecurityContextHolder.getContext().setAuthentication(newAuth);
			}

			return new ResponseEntity<>(
					new ProfileResponse(
							updated.getUserId(),
							updated.getUsername(),
							updated.getDisplayName(),
							updated.getNumChildren(),
							updated.getChildNames(),
							updated.getChildAges()
					),
					HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalStateException ex) {
			// Used for conflicts (username taken) or not found.
			if ("That username is already taken.".equals(ex.getMessage())) {
				return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
			}
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/me/password")
	public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return new ResponseEntity<>("Not authenticated.", HttpStatus.UNAUTHORIZED);
		}

		try {
			userService.updatePassword(authentication.getName(), request.getCurrentPassword(), request.getNewPassword());
			return new ResponseEntity<>("Password updated.", HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalStateException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}


