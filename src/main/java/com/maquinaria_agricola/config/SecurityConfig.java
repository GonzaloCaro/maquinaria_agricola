package com.maquinaria_agricola.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.maquinaria_agricola.service.usuario.CustomUserDetailsService;
import com.maquinaria_agricola.service.usuario.UsuarioService;
import com.maquinaria_agricola.utils.JwtAuthenticationFilter;
import com.maquinaria_agricola.utils.JwtUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UsuarioService usuarioService;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtils jwtUtils,
            UsuarioService usuarioService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.usuarioService = usuarioService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(
                                "/",
                                "/home",
                                "/home.html",
                                "/login",
                                "/register",
                                "/forgot-password",
                                "/api/auth/**",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/assets/**")
                        .permitAll()
                        // Todo lo demás requiere autenticación
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        // Registrar el filtro JWT
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtils, userDetailsService, usuarioService),
                UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self'; font-src 'self'; object-src 'none'; frame-ancestors 'none'; form-action 'self'; base-uri 'self';"))
                        .xssProtection(xss -> xss.disable())
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .referrerPolicy(referrer -> referrer.policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE)));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
