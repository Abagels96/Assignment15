package com.coderscampus.Assignment15.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;
    private static final String GOOGLE_PROVIDER = "google";

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to default OidcUserService to get the OidcUser from Google
        OidcUser oidcUser = super.loadUser(userRequest);

        // Extract user info from OIDC claims
        String email = oidcUser.getEmail();
        String displayName = oidcUser.getFullName();
        String sub = oidcUser.getSubject(); // Google's unique user ID

        System.out.println("=== OAuth2/OIDC User Loading ===");
        System.out.println("Email: " + email);
        System.out.println("Name: " + displayName);
        System.out.println("Sub (OAuth ID): " + sub);

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email not provided by OAuth provider");
        }

        // Try to find existing user by OAuth ID first, then by email
        Optional<User> existingUser = userRepository.findByOauthProviderAndOauthId(GOOGLE_PROVIDER, sub);
        if (existingUser.isEmpty()) {
            existingUser = userRepository.findByEmail(email);
        }

        User user;
        if (existingUser.isPresent()) {
            System.out.println("Found existing user: " + existingUser.get().getUsername());
            user = existingUser.get();
            if (user.getOauthProvider() == null) {
                user.setOauthProvider(GOOGLE_PROVIDER);
                user.setOauthId(sub);
            }
            if (user.getEmail() == null) {
                user.setEmail(email);
            }
            if (user.getDisplayName() == null && displayName != null) {
                user.setDisplayName(displayName);
            }
            if (user.getUsername() == null || user.getUsername().isBlank()) {
                user.setUsername(email);
            }
        } else {
            System.out.println("Creating new OAuth user with email: " + email);
            user = new User();
            user.setEmail(email);
            user.setOauthProvider(GOOGLE_PROVIDER);
            user.setOauthId(sub);
            user.setUsername(email); // Use email as username for OAuth users
            user.setDisplayName(displayName != null ? displayName : email);
            user.setNumChildren(0);
            user.setChildAges("");
            user.setChildNames("");
            user.setPassword(null); // OAuth users don't have passwords
        }

        try {
            user = userRepository.save(user);
            System.out.println("User saved successfully. ID: " + user.getUserId() + ", Username: " + user.getUsername());
        } catch (Exception e) {
            System.err.println("ERROR saving OAuth user: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("Failed to save user: " + e.getMessage());
        }

        // Return a custom OidcUser that uses email as getName() (the principal)
        // so it matches our User entity's username
        return new CustomOidcUser(oidcUser, email);
    }

    /**
     * Custom OidcUser that ensures getName() returns the email
     * (which matches our User entity's username).
     * Implements Serializable so Spring Session JDBC can persist the security context.
     */
    private static class CustomOidcUser implements OidcUser, Serializable {
        private static final long serialVersionUID = 1L;

        private final String email;
        private final OidcIdToken idToken;
        private final OidcUserInfo userInfo;
        private final Map<String, Object> attributes;
        private final Set<SimpleGrantedAuthority> authorities;
        private final Map<String, Object> claims;

        public CustomOidcUser(OidcUser delegate, String email) {
            this.email = email;
            this.idToken = delegate.getIdToken();
            this.userInfo = delegate.getUserInfo();
            // Copy to serializable maps
            this.attributes = new HashMap<>(delegate.getAttributes());
            this.claims = new HashMap<>(delegate.getClaims());
            this.authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getName() {
            return email;
        }

        @Override
        public Map<String, Object> getClaims() {
            return claims;
        }

        @Override
        public OidcUserInfo getUserInfo() {
            return userInfo;
        }

        @Override
        public OidcIdToken getIdToken() {
            return idToken;
        }
    }
}
