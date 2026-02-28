package com.backend.users.security;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.CollectionUtils;

import com.backend.core.security.CustomAccessDeniedHandler;
import com.backend.core.security.CustomAuthenticationEntryPoint;
import com.backend.core.security.JwtTokenAuthenticationFilter;
import com.backend.core.security.SecuritySettings;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final SecuritySettings securitySettings;
  private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
  private final ReactiveUserDetailsService userDetailsService;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

    configureOpenPaths(http);

    http.authorizeExchange(
            exchange ->
                exchange.pathMatchers("**/internal/**").permitAll().anyExchange().authenticated())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .addFilterAt(jwtTokenAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

    return http.build();
  }

  @Bean
  public ReactiveAuthenticationManager authenticationManager() {
    UserDetailsRepositoryReactiveAuthenticationManager authManager =
        new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authManager.setPasswordEncoder(passwordEncoder());
    return authManager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private void configureOpenPaths(ServerHttpSecurity http) {
    Set<SecuritySettings.OpenPath> openPaths = securitySettings.getOpenPaths();
    if (CollectionUtils.isEmpty(openPaths)) {
      return;
    }

    http.authorizeExchange(
        exchange ->
            openPaths.forEach(
                openPath -> {
                  String pattern = openPath.getPattern();
                  if (CollectionUtils.isEmpty(openPath.getMethods())) {
                    exchange.pathMatchers(pattern).permitAll();
                  } else {
                    openPath
                        .getMethods()
                        .forEach(
                            method ->
                                exchange
                                    .pathMatchers(HttpMethod.valueOf(method.name()), pattern)
                                    .permitAll());
                  }
                }));
  }
}
