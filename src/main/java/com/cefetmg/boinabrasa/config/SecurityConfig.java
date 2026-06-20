package com.cefetmg.boinabrasa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilita o CSRF para permitir POST, PUT e DELETE via Insomnia
            .csrf(csrf -> csrf.disable())
            
            // 2. Exige autenticação para qualquer requisição
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            
            // 3. Ativa o protocolo HTTP Basic Auth (usando o admin/123 que configuramos)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}