package com.example.demo.controller;

import com.example.demo.doc.PerfilControllerDoc;
import com.example.demo.dto.request.CreatePerfilRequest;
import com.example.demo.dto.request.UpdatePerfilRequest;
import com.example.demo.dto.response.PerfilResponse;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.service.PerfilService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code @AuthenticationPrincipal Usuario} é a mesma conta que o
 * {@code JwtAuthenticationFilter} colocou no {@code SecurityContextHolder}
 * ao validar o token — por isso cada endpoint só opera sobre os perfis de
 * quem está logado, sem precisar (e sem confiar) em nenhum id de usuário
 * vindo do corpo da requisição.
 */
@RestController
@RequestMapping("/perfis")
public class PerfilController implements PerfilControllerDoc {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<List<PerfilResponse>> listar(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        List<PerfilResponse> perfis = perfilService.listarDaConta(usuarioAutenticado).stream()
                .map(PerfilController::toResponse)
                .toList();
        return ResponseEntity.ok(perfis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> buscarPorId(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(toResponse(perfilService.buscarPorId(usuarioAutenticado, id)));
    }

    @PostMapping
    public ResponseEntity<PerfilResponse> criar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                  @Valid @RequestBody CreatePerfilRequest request) {
        Perfil perfil = perfilService.criar(usuarioAutenticado, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(perfil));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponse> atualizar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody UpdatePerfilRequest request) {
        Perfil perfil = perfilService.atualizar(usuarioAutenticado, id, request);
        return ResponseEntity.ok(toResponse(perfil));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                          @PathVariable Long id) {
        perfilService.deletar(usuarioAutenticado, id);
        return ResponseEntity.noContent().build();
    }

    private static PerfilResponse toResponse(Perfil perfil) {
        return new PerfilResponse(perfil.getId(), perfil.getUsuario().getId(), perfil.getNomePerfil(),
                perfil.getAvatarUrl(), perfil.getTipo(), perfil.getCriadoEm());
    }
}
