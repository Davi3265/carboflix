package com.example.demo.service;

import com.example.demo.dto.request.CreatePerfilRequest;
import com.example.demo.dto.request.UpdatePerfilRequest;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.repository.PerfilRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Perfis pertencem a uma conta ({@code Usuario}): toda consulta já sai
 * filtrada pelo usuário autenticado, e criar, atualizar ou remover um
 * perfil que pertence a outra conta é tratado como "não encontrado" (404),
 * nunca como "proibido" — assim não revelamos que o id pertence a outra
 * conta.
 */
@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final int limitePerfisPorConta;

    public PerfilService(PerfilRepository perfilRepository,
                          @Value("${app.perfis.limite-por-conta:5}") int limitePerfisPorConta) {
        this.perfilRepository = perfilRepository;
        this.limitePerfisPorConta = limitePerfisPorConta;
    }

    @Transactional(readOnly = true)
    public List<Perfil> listarDaConta(Usuario usuarioAutenticado) {
        return perfilRepository.findByUsuarioId(usuarioAutenticado.getId());
    }

    @Transactional(readOnly = true)
    public Perfil buscarPorId(Usuario usuarioAutenticado, Long id) {
        return perfilRepository.findByIdAndUsuarioId(id, usuarioAutenticado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil não encontrado."));
    }

    @Transactional
    public Perfil criar(Usuario usuarioAutenticado, CreatePerfilRequest request) {
        long totalAtual = perfilRepository.countByUsuarioId(usuarioAutenticado.getId());
        if (totalAtual >= limitePerfisPorConta) {
            throw new RegraNegocioException(
                    "Limite de " + limitePerfisPorConta + " perfis por conta atingido.");
        }

        Perfil perfil = new Perfil();
        perfil.setUsuario(usuarioAutenticado);
        perfil.setNomePerfil(request.nomePerfil());
        perfil.setAvatarUrl(request.avatarUrl());
        perfil.setTipo(request.tipo());
        return perfilRepository.save(perfil);
    }

    @Transactional
    public Perfil atualizar(Usuario usuarioAutenticado, Long id, UpdatePerfilRequest request) {
        Perfil perfil = buscarPorId(usuarioAutenticado, id);
        perfil.setNomePerfil(request.nomePerfil());
        perfil.setAvatarUrl(request.avatarUrl());
        perfil.setTipo(request.tipo());
        return perfilRepository.save(perfil);
    }

    @Transactional
    public void deletar(Usuario usuarioAutenticado, Long id) {
        Perfil perfil = buscarPorId(usuarioAutenticado, id);
        perfilRepository.delete(perfil);
    }
}
