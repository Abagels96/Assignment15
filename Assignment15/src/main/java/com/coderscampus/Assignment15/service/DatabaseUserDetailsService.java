package com.coderscampus.Assignment15.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public DatabaseUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    // OAuth users don't have passwords - they authenticate via OAuth2
    // If password is null, use a placeholder that will never match
    // This prevents form login for OAuth-only users
    String password = user.getPassword();
    if (password == null || password.isBlank()) {
      // Use a BCrypt hash that will never match any input
      // This effectively disables password-based login for OAuth users
      password = "$2a$10$NONE.OAUTH.USER.PASSWORD.HASH.NEVER.MATCHES";
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        password, // this should already be BCrypt-hashed from your register flow
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
  }
}




