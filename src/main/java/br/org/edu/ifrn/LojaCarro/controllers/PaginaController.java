package br.org.edu.ifrn.LojaCarro.controllers;

import br.org.edu.ifrn.LojaCarro.dto.UsuarioResponse;
import br.org.edu.ifrn.LojaCarro.services.CarroService;
import br.org.edu.ifrn.LojaCarro.services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    private final CarroService carroService;
    private final UsuarioService usuarioService;

    public PaginaController(
            CarroService carroService,
            UsuarioService usuarioService
    ) {
        this.carroService = carroService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(Model model, Authentication authentication) {
        model.addAttribute("carros", carroService.findAll());
        model.addAttribute("email", authentication.getName());
        model.addAttribute(
                "podeGerenciarCarros",
                possuiPapel(authentication, "FUNCIONARIO")
                        || possuiPapel(authentication, "ADMIN")
        );
        model.addAttribute(
                "administrador",
                possuiPapel(authentication, "ADMIN")
        );

        return "inicio";
    }

    @GetMapping("/gestao/carros")
    public String gerenciarCarros(Model model) {
        model.addAttribute("carros", carroService.findAll());
        return "gestao-carros";
    }

    @GetMapping("/admin/usuarios")
    public String gerenciarUsuarios(Model model) {
        model.addAttribute(
                "usuarios",
                usuarioService.findAll()
                        .stream()
                        .map(UsuarioResponse::new)
                        .toList()
        );

        return "admin/usuarios";
    }

    private boolean possuiPapel(Authentication authentication, String papel) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + papel)
                );
    }
}