package com.example.demo.service;

import com.example.demo.dto.request.CreatePerfilRequest;
import com.example.demo.dto.request.UpdatePerfilRequest;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.enums.TipoPerfil;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.repository.PerfilRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerfilServiceTests {

    private PerfilRepository repository;
    private PerfilService service;
    private Usuario conta;

    @BeforeEach
    void configurar() {
        repository = mock(PerfilRepository.class);
        service = new PerfilService(repository, 5);
        conta = new Usuario();
        conta.setId(10L);
    }

    @Test
    void criaPerfilParaAContaAutenticada() {
        when(repository.countByUsuarioId(10L)).thenReturn(4L);
        when(repository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Perfil criado = service.criar(conta, new CreatePerfilRequest("Davi", null, TipoPerfil.ADULTO));

        assertSame(conta, criado.getUsuario());
        assertEquals("Davi", criado.getNomePerfil());
        assertEquals(TipoPerfil.ADULTO, criado.getTipo());
    }

    @Test
    void rejeitaOSextoPerfilSemSalvar() {
        when(repository.countByUsuarioId(10L)).thenReturn(5L);

        assertThrows(RegraNegocioException.class,
                () -> service.criar(conta, new CreatePerfilRequest("Novo", null, TipoPerfil.INFANTIL)));

        verify(repository, never()).save(any(Perfil.class));
    }

    @Test
    void respeitaOLimiteConfigurado() {
        PerfilService limiteDois = new PerfilService(repository, 2);
        when(repository.countByUsuarioId(10L)).thenReturn(2L);

        assertThrows(RegraNegocioException.class,
                () -> limiteDois.criar(conta, new CreatePerfilRequest("Novo", null, TipoPerfil.ADULTO)));
        verify(repository, never()).save(any(Perfil.class));
    }

    @Test
    void atualizaOsDadosSemTrocarOProprietario() {
        Perfil perfil = perfilDaConta();
        when(repository.findByIdAndUsuarioId(1L, 10L)).thenReturn(Optional.of(perfil));
        when(repository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.atualizar(conta, 1L, new UpdatePerfilRequest("Infantil", "https://exemplo.com/avatar.png",
                TipoPerfil.INFANTIL));

        ArgumentCaptor<Perfil> salvo = ArgumentCaptor.forClass(Perfil.class);
        verify(repository).save(salvo.capture());
        assertSame(conta, salvo.getValue().getUsuario());
        assertEquals("Infantil", salvo.getValue().getNomePerfil());
        assertEquals(TipoPerfil.INFANTIL, salvo.getValue().getTipo());
        assertEquals("https://exemplo.com/avatar.png", salvo.getValue().getAvatarUrl());
    }

    @Test
    void naoAtualizaPerfilAusenteNaConta() {
        when(repository.findByIdAndUsuarioId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizar(conta, 99L, new UpdatePerfilRequest("Nome", null, TipoPerfil.ADULTO)));

        verify(repository, never()).save(any(Perfil.class));
    }

    @Test
    void naoRemovePerfilAusenteNaConta() {
        when(repository.findByIdAndUsuarioId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(conta, 99L));

        verify(repository, never()).delete(any(Perfil.class));
    }

    @Test
    void removePerfilDaPropriaConta() {
        Perfil perfil = perfilDaConta();
        when(repository.findByIdAndUsuarioId(1L, 10L)).thenReturn(Optional.of(perfil));

        service.deletar(conta, 1L);

        verify(repository).delete(perfil);
    }

    private Perfil perfilDaConta() {
        Perfil perfil = new Perfil();
        perfil.setId(1L);
        perfil.setUsuario(conta);
        perfil.setNomePerfil("Davi");
        perfil.setTipo(TipoPerfil.ADULTO);
        return perfil;
    }
}
