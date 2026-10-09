package com.example.demo.controller;

import com.example.demo.doc.UsuarioControllerDoc;
import com.example.demo.dto.request.UpdateUsuarioRequest;
import com.example.demo.dto.response.UsuarioResponse;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD de {@code Usuario} (criar e autenticar continuam no
 * {@code AuthController}, em {@code /auth}). Cada conta autenticada acessa
 * somente seus dados; o Service confere o id da rota antes de consultar ou alterar.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController implements UsuarioControllerDoc {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        List<UsuarioResponse> usuarios = usuarioService.listarDaConta(usuarioAutenticado).stream()
                .map(UsuarioController::toResponse)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(toResponse(usuarioService.buscarPorId(usuarioAutenticado, id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(toResponse(usuarioService.atualizar(usuarioAutenticado, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@AuthenticationPrincipal Usuario usuarioAutenticado, @PathVariable Long id) {
        usuarioService.deletar(usuarioAutenticado, id);
        return ResponseEntity.noContent().build();
    }

    private static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getRole(), usuario.getCriadoEm());
    }
}
