package com.example.demo.controller;

import com.example.demo.doc.UsuarioControllerDoc;
import com.example.demo.dto.request.UpdateUsuarioRequest;
import com.example.demo.dto.response.UsuarioResponse;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD de {@code Usuario} (criar e autenticar continuam no
 * {@code AuthController}, em {@code /auth}). Qualquer usuário autenticado
 * pode listar, ver, atualizar ou remover qualquer conta — não há ainda
 * papéis (roles) diferentes ou uma checagem de "só a própria conta", então
 * isso fica como um próximo passo natural (ex.: {@code @PreAuthorize} com
 * uma ROLE_ADMIN, ou comparar o id da rota com o usuário autenticado).
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController implements UsuarioControllerDoc {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos().stream()
                .map(UsuarioController::toResponse)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(usuarioService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(toResponse(usuarioService.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getRole(), usuario.getCriadoEm());
    }
}
