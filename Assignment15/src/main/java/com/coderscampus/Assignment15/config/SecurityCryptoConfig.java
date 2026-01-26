package com.coderscampus.Assignment15.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityCryptoConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    DelegatingPasswordEncoder encoder =
        (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // Allows matching passwords stored as raw BCrypt like "$2a$..." (no "{bcrypt}" prefix)
    encoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

    return encoder;
  }
}