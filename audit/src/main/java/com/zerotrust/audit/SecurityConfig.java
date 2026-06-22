package com.zerotrust.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				// Permet aux navigateurs de valider les droits CORS (Preflight) sans jeton
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
			);
		return http.build();
	}
	
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                // Va chercher le bloc "realm_access" injecté par Keycloak
                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                if (realmAccess == null || !realmAccess.containsKey("roles")) {
                    return List.of();
                }
                
                List<String> roles = (List<String>) realmAccess.get("roles");
                
                // Mappe chaque rôle texte (ex: "role signer") en autorité Spring Security
                return roles.stream()
                        .map(roleName -> new SimpleGrantedAuthority(roleName.trim()))
                        .collect(Collectors.toList());
            }
        });
        return converter;
    }
}