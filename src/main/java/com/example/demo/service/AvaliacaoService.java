package com.example.demo.service;

import com.example.demo.dto.request.CreateAvaliacaoRequest;
import com.example.demo.dto.request.UpdateAvaliacaoRequest;
import com.example.demo.entity.Avaliacao;
import com.example.demo.entity.Filme;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.FilmeRepository;
import com.example.demo.repository.PerfilRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Criar, atualizar e remover uma avaliação exigem que o perfil usado
 * pertença à conta autenticada (senão não seria possível garantir "esse
 * perfil só avalia o mesmo filme uma vez" de forma confiável — qualquer um
 * poderia avaliar usando o perfil de outra conta). Já listar e buscar por
 * id são leitura aberta a qualquer usuário autenticado, como avaliações
 * públicas dentro do app.
 */
@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PerfilRepository perfilRepository;
    private final FilmeRepository filmeRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                             PerfilRepository perfilRepository,
                             FilmeRepository filmeRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.perfilRepository = perfilRepository;
        this.filmeRepository = filmeRepository;
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação não encontrada."));
    }

    @Transactional
    public Avaliacao criar(Usuario usuarioAutenticado, CreateAvaliacaoRequest request) {
        Perfil perfil = buscarPerfilDaConta(usuarioAutenticado, request.perfilId());

        Filme filme = filmeRepository.findById(request.filmeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado."));

        if (avaliacaoRepository.existsByPerfilIdAndFilmeId(perfil.getId(), filme.getId())) {
            throw new RegraNegocioException("Este perfil já avaliou este filme.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPerfil(perfil);
        avaliacao.setFilme(filme);
        avaliacao.setNota(request.nota());
        avaliacao.setComentario(request.comentario());
        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public Avaliacao atualizar(Usuario usuarioAutenticado, Long id, UpdateAvaliacaoRequest request) {
        Avaliacao avaliacao = buscarPorIdDaConta(usuarioAutenticado, id);
        avaliacao.setNota(request.nota());
        avaliacao.setComentario(request.comentario());
        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public void deletar(Usuario usuarioAutenticado, Long id) {
        Avaliacao avaliacao = buscarPorIdDaConta(usuarioAutenticado, id);
        avaliacaoRepository.delete(avaliacao);
    }

    private Perfil buscarPerfilDaConta(Usuario usuarioAutenticado, Long perfilId) {
        return perfilRepository.findByIdAndUsuarioId(perfilId, usuarioAutenticado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado."));
    }

    /** Busca a avaliação garantindo que o perfil dela é da conta autenticada. */
    private Avaliacao buscarPorIdDaConta(Usuario usuarioAutenticado, Long id) {
        Avaliacao avaliacao = buscarPorId(id);
        if (!avaliacao.getPerfil().getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new RecursoNaoEncontradoException("Avaliação não encontrada.");
        }
        return avaliacao;
    }
}
