package com.coderscampus.Assignment15.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coderscampus.Assignment15.domain.User;
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
}


