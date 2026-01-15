package com.backend.users.security;

import com.backend.core.security.CustomAccessDeniedHandler;
import com.backend.core.security.CustomAuthenticationEntryPoint;
import com.backend.core.security.JwtTokenAuthenticationFilter;
import com.backend.core.security.SecuritySettings;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecuritySettings securitySettings;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        configureOpenPaths(http);

        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void configureOpenPaths(HttpSecurity http) throws Exception {
        Set<SecuritySettings.OpenPath> openPaths = securitySettings.getOpenPaths();
        if (CollectionUtils.isEmpty(openPaths)) {
            return;
        }

        http.authorizeHttpRequests(auth ->
            openPaths.forEach(openPath -> permitOpenPath(auth, openPath))
        );
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(new AntPathRequestMatcher("**/internal/**")).permitAll()
        );
    }

    private void permitOpenPath(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
            SecuritySettings.OpenPath openPath) {
        String pattern = openPath.getPattern();

        if (CollectionUtils.isEmpty(openPath.getMethods())) {
            auth.requestMatchers(new AntPathRequestMatcher(pattern)).permitAll();
            return;
        }
        openPath.getMethods().forEach(method ->
                auth.requestMatchers(new AntPathRequestMatcher(pattern, method.name())).permitAll()
        );
    }
}