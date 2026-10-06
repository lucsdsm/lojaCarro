package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Papel;
import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario criar(String nome, String email, String senha, Papel papel) {
        String emailNormalizado = normalizarEmail(email);

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }

        validarDados(nome, emailNormalizado, senha, papel);

        Usuario usuario = new Usuario(
                nome.trim(),
                emailNormalizado,
                passwordEncoder.encode(senha),
                papel
        );

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(
            Long id,
            String nome,
            String email,
            Papel papel,
            boolean ativo,
            String novaSenha
    ) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        String emailNormalizado = normalizarEmail(email);

        usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .filter(outroUsuario -> !outroUsuario.getId().equals(id))
                .ifPresent(outroUsuario -> {
                    throw new IllegalArgumentException("Já existe outro usuário com este e-mail.");
                });

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (papel == null) {
            throw new IllegalArgumentException("Papel é obrigatório.");
        }

        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setPapel(papel);
        usuario.setAtivo(ativo);

        if (novaSenha != null && !novaSenha.isBlank()) {
            if (novaSenha.length() < 6) {
                throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
            }

            usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        usuarioRepository.deleteById(id);
    }

    private void validarDados(String nome, String email, String senha, Papel papel) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }

        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        if (papel == null) {
            throw new IllegalArgumentException("Papel é obrigatório.");
        }
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}