package br.org.edu.ifrn.LojaCarro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.org.edu.ifrn.LojaCarro.services.LogSistemaService;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LogSistemaService logSistemaService
    ) throws Exception {
        return http
                .addFilterAfter(
                        new LogSistemaFilter(logSistemaService),
                        SecurityContextHolderFilter.class
                )

                .authorizeHttpRequests(auth -> auth

                        // Recursos públicos e página de autenticação
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Administração completa de usuários: apenas ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Leitura de carros: qualquer usuário autenticado
                        .requestMatchers(HttpMethod.GET, "/carro/**")
                        .hasAnyRole("CLIENTE", "FUNCIONARIO", "ADMIN")

                        // Cadastro e edição: funcionário ou administrador
                        .requestMatchers(HttpMethod.POST, "/carro/salvar")
                        .hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/carro/**")
                        .hasAnyRole("FUNCIONARIO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/carro/updateCarro")
                        .hasAnyRole("FUNCIONARIO", "ADMIN")

                        // Exclusão: apenas administrador
                        .requestMatchers(HttpMethod.DELETE, "/carro/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/carro/deleteCarro")
                        .hasRole("ADMIN")

                        // Esta rota deve ser removida ou protegida.
                        // Durante desenvolvimento, deixe apenas ADMIN usá-la:
                        .requestMatchers("/carro/teste").hasRole("ADMIN")

                        // Qualquer outra rota exige login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado")
                )
                .build();
    }
}