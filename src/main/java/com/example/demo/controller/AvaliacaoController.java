package com.example.demo.controller;

import com.example.demo.dto.request.CreateAvaliacaoRequest;
import com.example.demo.dto.request.UpdateAvaliacaoRequest;
import com.example.demo.dto.response.AvaliacaoResponse;
import com.example.demo.entity.Avaliacao;
import com.example.demo.entity.Usuario;
import com.example.demo.service.AvaliacaoService;
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

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponse>> listar() {
        List<AvaliacaoResponse> avaliacoes = avaliacaoService.listarTodas().stream()
                .map(AvaliacaoController::toResponse)
                .toList();
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(avaliacaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                      @Valid @RequestBody CreateAvaliacaoRequest request) {
        Avaliacao avaliacao = avaliacaoService.criar(usuarioAutenticado, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(avaliacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponse> atualizar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                          @PathVariable Long id,
                                                          @Valid @RequestBody UpdateAvaliacaoRequest request) {
        Avaliacao avaliacao = avaliacaoService.atualizar(usuarioAutenticado, id, request);
        return ResponseEntity.ok(toResponse(avaliacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                          @PathVariable Long id) {
        avaliacaoService.deletar(usuarioAutenticado, id);
        return ResponseEntity.noContent().build();
    }

    private static AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getPerfil().getId(),
                avaliacao.getPerfil().getNomePerfil(),
                avaliacao.getFilme().getId(),
                avaliacao.getFilme().getTitulo(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm());
    }
}
