package com.coderscampus.Assignment15.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // Bootcamp simplification: JS fetch calls + custom login form without CSRF token plumbing.
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        // Allow Spring Boot static resources from classpath:/static, /public, /resources, /META-INF/resources
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        // Explicit root-level static files (your JS files are at "/login.js", not "/js/login.js")
        .requestMatchers(
            "/login.js",
            "/register.js",
            "/profile.js",
            "/track.js",
            "/progress.js",
            "/timeline.js",
            "/favicon.ico"
        ).permitAll()
        // Public pages + OAuth2 endpoints
        .requestMatchers(
            "/login",
            "/register",
            "/auth/register",
            "/oauth2/**",
            "/login/oauth2/**",
            "/error"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/track", true)
        .failureUrl("/login?error=true")
        .permitAll()
      )
      .oauth2Login(oauth2 -> oauth2
        .loginPage("/login")
        .defaultSuccessUrl("/track", true)
        .failureUrl("/login?error=true")
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout=true")
      );

    return http.build();
  }
}