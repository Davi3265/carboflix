package com.example.demo.controller;

import com.example.demo.doc.FilmeControllerDoc;
import com.example.demo.dto.request.CreateFilmeRequest;
import com.example.demo.dto.request.UpdateFilmeRequest;
import com.example.demo.dto.response.CategoriaResponse;
import com.example.demo.dto.response.FilmeResponse;
import com.example.demo.entity.Filme;
import com.example.demo.service.FilmeService;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filmes")
public class FilmeController implements FilmeControllerDoc {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @GetMapping
    public ResponseEntity<List<FilmeResponse>> listar() {
        List<FilmeResponse> filmes = filmeService.listarTodos().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(filmes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(filmeService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<FilmeResponse> criar(@Valid @RequestBody CreateFilmeRequest request) {
        Filme filme = filmeService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(filme));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateFilmeRequest request) {
        Filme filme = filmeService.atualizar(id, request);
        return ResponseEntity.ok(toResponse(filme));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        filmeService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private FilmeResponse toResponse(Filme filme) {
        Set<CategoriaResponse> categorias = filme.getCategorias().stream()
                .map(CategoriaController::toResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Double media = filmeService.calcularMedia(filme.getId());
        long totalAvaliacoes = filmeService.contarAvaliacoes(filme.getId());

        return new FilmeResponse(filme.getId(), filme.getTitulo(), filme.getSinopse(), filme.getAnoLancamento(),
                filme.getDuracaoMin(), filme.getClassificacaoIndicativa(), filme.getPosterUrl(), categorias,
                media, totalAvaliacoes);
    }
}
