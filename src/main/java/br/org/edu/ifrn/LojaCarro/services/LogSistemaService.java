package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.LogSistema;
import br.org.edu.ifrn.LojaCarro.repository.LogSistemaRepository;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogSistemaService {

    private final LogSistemaRepository logRepository;
    private final UsuarioRepository usuarioRepository;

    public LogSistemaService(
            LogSistemaRepository logRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.logRepository = logRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Long buscarUsuarioId(String email) {
        if (email == null) {
            return null;
        }

        return usuarioRepository.findByEmailIgnoreCase(email)
                .map(usuario -> usuario.getId())
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Long usuarioId, String acao) {
        String acaoTratada = acao
                .replace('\r', '_')
                .replace('\n', '_');

        if (acaoTratada.length() > 500) {
            acaoTratada = acaoTratada.substring(0, 500);
        }

        logRepository.save(new LogSistema(usuarioId, acaoTratada));
    }
}