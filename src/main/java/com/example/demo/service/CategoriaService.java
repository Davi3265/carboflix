package com.example.demo.service;

import com.example.demo.dto.request.CreateCategoriaRequest;
import com.example.demo.dto.request.UpdateCategoriaRequest;
import com.example.demo.entity.Categoria;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
    }

    @Transactional
    public Categoria criar(CreateCategoriaRequest request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Já existe uma categoria com esse nome.");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, UpdateCategoriaRequest request) {
        Categoria categoria = buscarPorId(id);

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new RegraNegocioException("Já existe uma categoria com esse nome.");
        }

        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}
