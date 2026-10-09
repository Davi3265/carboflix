package com.example.demo.controller;

import com.example.demo.dto.request.CreatePerfilRequest;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.enums.TipoPerfil;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.service.PerfilService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PerfilControllerTests {

    private PerfilService service;
    private MockMvc mvc;
    private Usuario conta;

    @BeforeEach
    void configurar() {
        service = mock(PerfilService.class);
        mvc = MockMvcBuilders.standaloneSetup(new PerfilController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        conta = new Usuario();
        conta.setId(10L);
        conta.setSenhaHash("hash-que-nao-pode-aparecer-na-resposta");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(conta, null, List.of()));
    }

    @AfterEach
    void limparAutenticacao() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criarResponde201ComDtoSemExporSenha() throws Exception {
        Perfil perfil = new Perfil();
        perfil.setId(1L);
        perfil.setUsuario(conta);
        perfil.setNomePerfil("Davi");
        perfil.setTipo(TipoPerfil.ADULTO);
        perfil.setCriadoEm(LocalDateTime.of(2026, 10, 8, 20, 0));
        when(service.criar(eq(conta), any(CreatePerfilRequest.class))).thenReturn(perfil);

        mvc.perform(post("/perfis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomePerfil\":\"Davi\",\"tipo\":\"ADULTO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.nomePerfil").value("Davi"))
                .andExpect(jsonPath("$.usuario").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void nomeVazioETipoAusenteRetornam400SemChamarService() throws Exception {
        mvc.perform(post("/perfis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomePerfil\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nomePerfil").exists())
                .andExpect(jsonPath("$.campos.tipo").exists());
        verifyNoInteractions(service);
    }

    @Test
    void enumInvalidoRetorna400SemChamarService() throws Exception {
        mvc.perform(post("/perfis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomePerfil\":\"Davi\",\"tipo\":\"OUTRO\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void perfilAusenteNaContaRetorna404() throws Exception {
        when(service.buscarPorId(conta, 99L))
                .thenThrow(new RecursoNaoEncontradoException("Perfil não encontrado."));

        mvc.perform(get("/perfis/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Perfil não encontrado."));
    }
}
