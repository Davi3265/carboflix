package com.example.demo.service;

import com.example.demo.dto.request.CreateFilmeRequest;
import com.example.demo.dto.request.UpdateFilmeRequest;
import com.example.demo.entity.Categoria;
import com.example.demo.entity.Filme;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.FilmeRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Além do CRUD de {@code Filme}, calcula a média das avaliações (usado
 * pelo Controller para montar o {@code FilmeResponse}) — por isso depende
 * do {@link AvaliacaoRepository}, e não só do {@link FilmeRepository}.
 */
@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final CategoriaRepository categoriaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public FilmeService(FilmeRepository filmeRepository,
                         CategoriaRepository categoriaRepository,
                         AvaliacaoRepository avaliacaoRepository) {
        this.filmeRepository = filmeRepository;
        this.categoriaRepository = categoriaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Filme> listarTodos() {
        return filmeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Filme buscarPorId(Long id) {
        return filmeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado."));
    }

    @Transactional
    public Filme criar(CreateFilmeRequest request) {
        Filme filme = new Filme();
        preencher(filme, request.titulo(), request.sinopse(), request.anoLancamento(), request.duracaoMin(),
                request.classificacaoIndicativa(), request.posterUrl(), request.categoriaIds());
        return filmeRepository.save(filme);
    }

    @Transactional
    public Filme atualizar(Long id, UpdateFilmeRequest request) {
        Filme filme = buscarPorId(id);
        preencher(filme, request.titulo(), request.sinopse(), request.anoLancamento(), request.duracaoMin(),
                request.classificacaoIndicativa(), request.posterUrl(), request.categoriaIds());
        return filmeRepository.save(filme);
    }

    @Transactional
    public void deletar(Long id) {
        Filme filme = buscarPorId(id);
        filmeRepository.delete(filme);
    }

    /**
     * Média das notas de {@code Avaliacao} para o filme, arredondada para
     * 1 casa decimal, ou {@code null} se ainda não houver avaliações.
     */
    @Transactional(readOnly = true)
    public Double calcularMedia(Long filmeId) {
        Double media = avaliacaoRepository.mediaNotasPorFilme(filmeId);
        return media == null ? null : Math.round(media * 10) / 10.0;
    }

    @Transactional(readOnly = true)
    public long contarAvaliacoes(Long filmeId) {
        return avaliacaoRepository.countByFilmeId(filmeId);
    }

    private void preencher(Filme filme, String titulo, String sinopse, Integer anoLancamento, Integer duracaoMin,
                            String classificacaoIndicativa, String posterUrl, Set<Long> categoriaIds) {
        filme.setTitulo(titulo);
        filme.setSinopse(sinopse);
        filme.setAnoLancamento(anoLancamento);
        filme.setDuracaoMin(duracaoMin);
        filme.setClassificacaoIndicativa(classificacaoIndicativa);
        filme.setPosterUrl(posterUrl);
        filme.setCategorias(resolverCategorias(categoriaIds));
    }

    private Set<Categoria> resolverCategorias(Set<Long> categoriaIds) {
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Categoria> encontradas = categoriaRepository.findAllById(categoriaIds);
        if (encontradas.size() != categoriaIds.size()) {
            Set<Long> idsEncontrados = encontradas.stream().map(Categoria::getId).collect(Collectors.toSet());
            Set<Long> idsFaltando = categoriaIds.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .collect(Collectors.toSet());
            throw new RecursoNaoEncontradoException("Categoria(s) não encontrada(s): " + idsFaltando);
        }

        return new LinkedHashSet<>(encontradas);
    }
}
