package br.org.edu.ifrn.LojaCarro.config;

import br.org.edu.ifrn.LojaCarro.services.LogSistemaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LogSistemaFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(LogSistemaFilter.class);

    private final LogSistemaService logService;

    public LogSistemaFilter(LogSistemaService logService) {
        this.logService = logService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Guarda o ID antes da operação, inclusive antes de excluir
        // a própria conta ou limpar o contexto durante o logout.
        Long usuarioIdInicial = buscarUsuarioIdAtual();
        boolean ocorreuExcecao = false;

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            ocorreuExcecao = true;
            throw ex;
        } finally {
            Long usuarioId = usuarioIdInicial;

            // No login, a autenticação pode surgir durante a requisição.
            if (usuarioId == null) {
                usuarioId = buscarUsuarioIdAtual();
            }

            String acao = request.getMethod()
                    + " "
                    + request.getServletPath()
                    + " | HTTP "
                    + response.getStatus();

            if (ocorreuExcecao) {
                acao += " | EXCECAO";
            }

            try {
                logService.registrar(usuarioId, acao);
            } catch (RuntimeException ex) {
                // Uma falha da auditoria não deve substituir o
                // resultado original da requisição nesta versão simples.
                logger.error(
                        "Não foi possível salvar o log de {} {}",
                        request.getMethod(),
                        request.getServletPath(),
                        ex
                );
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String caminho = request.getServletPath();

        return caminho.startsWith("/css/")
                || caminho.startsWith("/js/")
                || caminho.startsWith("/images/")
                || caminho.equals("/favicon.ico");
    }

    private Long buscarUsuarioIdAtual() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        try {
            return logService.buscarUsuarioId(authentication.getName());
        } catch (RuntimeException ex) {
            logger.warn(
                    "Não foi possível identificar o usuário para auditoria",
                    ex
            );

            return null;
        }
    }
}