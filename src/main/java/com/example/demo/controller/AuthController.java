package com.example.demo.controller;

import com.example.demo.config.TokenConfig;
import com.example.demo.doc.AuthControllerDoc;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterUserRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.RegisterUserResponse;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Só criar conta e autenticar ficam aqui; o CRUD de {@code Usuario}
 * (listar, buscar, atualizar, remover) está no {@code UsuarioController},
 * em {@code /usuarios}. O tratamento de erro (e-mail duplicado, credenciais
 * inválidas) foi centralizado no {@code GlobalExceptionHandler}, e a
 * documentação Swagger fica na interface {@code AuthControllerDoc}.
 */
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDoc {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UsuarioService usuarioService,
                           AuthenticationManager authenticationManager,
                           TokenConfig tokenConfig) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        Usuario novoUsuario = usuarioService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(novoUsuario.getNome(), novoUsuario.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken credenciais =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        Authentication authentication = authenticationManager.authenticate(credenciais);
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

        String token = tokenConfig.generateToken(usuarioAutenticado);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
