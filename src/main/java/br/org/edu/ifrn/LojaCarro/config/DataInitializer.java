package br.org.edu.ifrn.LojaCarro.config;

import br.org.edu.ifrn.LojaCarro.model.Papel;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner criarAdminInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String emailAdmin = "admin@lojacarro.local";

            if (!usuarioRepository.existsByEmailIgnoreCase(emailAdmin)) {
                Usuario admin = new Usuario(
                        "Administrador",
                        emailAdmin,
                        passwordEncoder.encode("admin123"),
                        Papel.ADMIN
                );

                usuarioRepository.save(admin);
            }
        };
    }
}