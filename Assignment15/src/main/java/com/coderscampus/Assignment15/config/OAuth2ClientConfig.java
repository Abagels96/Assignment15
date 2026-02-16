package com.coderscampus.Assignment15.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.CommonOAuth2Provider;
import org.springframework.util.StringUtils;

@Configuration
public class OAuth2ClientConfig {

  @Bean
  @ConditionalOnMissingBean(ClientRegistrationRepository.class)
  @ConditionalOnProperty(
      prefix = "spring.security.oauth2.client.registration.google",
      name = { "client-id", "client-secret" }
  )
  ClientRegistrationRepository clientRegistrationRepository(Environment environment) {
    String clientId = environment.getProperty("spring.security.oauth2.client.registration.google.client-id");
    String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.google.client-secret");
    String redirectUri = environment.getProperty("spring.security.oauth2.client.registration.google.redirect-uri");

    ClientRegistration.Builder builder = CommonOAuth2Provider.GOOGLE
        .getBuilder("google")
        .clientId(clientId)
        .clientSecret(clientSecret);

    if (StringUtils.hasText(redirectUri)) {
      builder.redirectUri(redirectUri);
    }

    ClientRegistration registration = builder.build();
    return new InMemoryClientRegistrationRepository(registration);
  }
}

