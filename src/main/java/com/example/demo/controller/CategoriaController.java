package com.example.demo.controller;

import com.example.demo.doc.CategoriaControllerDoc;
import com.example.demo.dto.request.CreateCategoriaRequest;
import com.example.demo.dto.request.UpdateCategoriaRequest;
import com.example.demo.dto.response.CategoriaResponse;
import com.example.demo.entity.Categoria;
import com.example.demo.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/categorias")
public class CategoriaController implements CategoriaControllerDoc {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        List<CategoriaResponse> categorias = categoriaService.listarTodas().stream()
                .map(CategoriaController::toResponse)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(categoriaService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CreateCategoriaRequest request) {
        Categoria categoria = categoriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateCategoriaRequest request) {
        Categoria categoria = categoriaService.atualizar(id, request);
        return ResponseEntity.ok(toResponse(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    static CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }
}
