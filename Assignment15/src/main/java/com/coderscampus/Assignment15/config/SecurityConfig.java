package com.coderscampus.Assignment15.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final com.coderscampus.Assignment15.service.CustomOAuth2UserService customOAuth2UserService;

  public SecurityConfig(com.coderscampus.Assignment15.service.CustomOAuth2UserService customOAuth2UserService) {
    this.customOAuth2UserService = customOAuth2UserService;
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository) throws Exception {
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
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout=true")
      );

    ClientRegistrationRepository registrationRepository = clientRegistrationRepository.getIfAvailable();
    if (registrationRepository != null
        && registrationRepository.findByRegistrationId("google") != null) {
      http.oauth2Login(oauth2 -> oauth2
        .loginPage("/login")
        .userInfoEndpoint(userInfo -> userInfo
            .oidcUserService(customOAuth2UserService))
        .defaultSuccessUrl("/track", true)
        .failureUrl("/login?error=true")
      );
    }

    return http.build();
  }
}