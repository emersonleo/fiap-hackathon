package br.com.fiap.postech.meu_postinho.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((req, res, exc2) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        
                        // Swagger/OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        
                        // UBS Endpoints (public)
                        .requestMatchers(HttpMethod.GET, "/api/ubs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ubs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/medicamentos/disponiveis").authenticated()
                        
                        // Morador endpoints
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/solicitacoes").hasRole("MORADOR")
                        .requestMatchers(HttpMethod.GET, "/api/agendamentos/meus").hasRole("MORADOR")
                        .requestMatchers(HttpMethod.POST, "/api/agendamentos").hasRole("MORADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/agendamentos/**").hasRole("MORADOR")
                        .requestMatchers(HttpMethod.GET, "/api/noticias/minhas").hasRole("MORADOR")
                        
                        // Agente endpoints
                        .requestMatchers(HttpMethod.POST, "/api/agentes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agentes/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.POST, "/api/estoques").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/estoques/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.GET, "/api/estoques/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.POST, "/api/vagas").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/vagas/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/vagas/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.GET, "/api/vagas/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/solicitacoes/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.POST, "/api/noticias").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/noticias/**").hasRole("AGENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/noticias/**").hasRole("AGENTE")
                        
                        // Admin endpoints
                        .requestMatchers("/api/ubs/importar").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
