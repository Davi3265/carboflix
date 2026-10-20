package com.example.demo;

import com.example.demo.config.TokenConfig;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.AvaliacaoRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.FilmeRepository;
import com.example.demo.repository.PerfilRepository;
import com.example.demo.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Contexto real de MVC, JWT e OpenAPI; somente os repositórios são simulados. */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration,"
                + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration",
        "DB_PASSWORD=apenas-teste-sem-banco",
        "jwt.secret=segredo-apenas-para-os-testes-locais"
})
@AutoConfigureMockMvc
class ApiRegressionTests {

    @Autowired MockMvc mvc;
    @Autowired TokenConfig tokens;
    @Autowired PasswordEncoder encoder;
    @MockitoBean UsuarioRepository usuarios;
    @MockitoBean PerfilRepository perfis;
    @MockitoBean FilmeRepository filmes;
    @MockitoBean CategoriaRepository categorias;
    @MockitoBean AvaliacaoRepository avaliacoes;

    private Usuario conta;
    private String bearer;

    @BeforeEach
    void configurarConta() {
        conta = new Usuario();
        conta.setId(10L);
        conta.setNome("Davi");
        conta.setEmail("davi@exemplo.com");
        conta.setSenhaHash(encoder.encode("senha123"));
        when(usuarios.findByEmail(conta.getEmail())).thenReturn(Optional.of(conta));
        when(usuarios.findById(conta.getId())).thenReturn(Optional.of(conta));
        when(usuarios.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        bearer = "Bearer " + tokens.generateToken(conta);
    }

    @Test
    void semTokenRetorna401NoFormatoDaApi() throws Exception {
        mvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensagem").value("Token ausente ou inválido."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void tokenInvalidoRetorna401() throws Exception {
        mvc.perform(get("/usuarios").header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listaSomenteAContaAutenticada() throws Exception {
        mvc.perform(get("/usuarios").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].senhaHash").doesNotExist());
        verify(usuarios, never()).findAll();
    }

    @Test
    void naoConsultaOutraConta() throws Exception {
        mvc.perform(get("/usuarios/20").header("Authorization", bearer))
                .andExpect(status().isNotFound());
        verify(usuarios, never()).findById(20L);
    }

    @Test
    void naoAlteraASenhaDeOutraConta() throws Exception {
        mvc.perform(put("/usuarios/20").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Outro\",\"email\":\"outro@exemplo.com\",\"senha\":\"nova123\"}"))
                .andExpect(status().isNotFound());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void naoRemoveOutraConta() throws Exception {
        mvc.perform(delete("/usuarios/20").header("Authorization", bearer))
                .andExpect(status().isNotFound());
        verify(usuarios, never()).delete(any(Usuario.class));
    }

    @Test
    void alteraASenhaDaPropriaConta() throws Exception {
        mvc.perform(put("/usuarios/10").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Davi\",\"email\":\"davi@exemplo.com\",\"senha\":\"nova123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
        assertTrue(encoder.matches("nova123", conta.getSenhaHash()));
    }

    @Test
    void removeAPropriaConta() throws Exception {
        mvc.perform(delete("/usuarios/10").header("Authorization", bearer))
                .andExpect(status().isNoContent());
        verify(usuarios).delete(conta);
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {
        mvc.perform(get("/usuarios/abc").header("Authorization", bearer))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void cadastroRejeitaNomeAcimaDoLimiteDoBanco() throws Exception {
        String body = "{\"nome\":\"" + "a".repeat(121)
                + "\",\"email\":\"novo@exemplo.com\",\"senha\":\"senha123\"}";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void cadastroRejeitaEmailAcimaDoLimiteDoBanco() throws Exception {
        String email = "a@" + ("a".repeat(60) + ".").repeat(4) + "exemplo.com";
        String body = "{\"nome\":\"Novo\",\"email\":\"" + email + "\",\"senha\":\"senha123\"}";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.email").exists());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void atualizacaoRejeitaNomeAcimaDoLimite() throws Exception {
        String body = "{\"nome\":\"" + "a".repeat(121) + "\",\"email\":\"davi@exemplo.com\"}";
        mvc.perform(put("/usuarios/10").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void cadastroContinuaPublicoESemExporSenha() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Novo\",\"email\":\"novo@exemplo.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void loginContinuaPublicoEDevolveToken() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"davi@exemplo.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void openApiIncluiAsCincoEntidadesEAuthSemExigirToken() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/usuarios'].get.tags[0]").value("Usuario"))
                .andExpect(jsonPath("$.paths['/perfis'].get.tags[0]").value("Perfil"))
                .andExpect(jsonPath("$.paths['/filmes'].get.tags[0]").value("Filme"))
                .andExpect(jsonPath("$.paths['/categorias'].get.tags[0]").value("Categoria"))
                .andExpect(jsonPath("$.paths['/avaliacoes'].get.tags[0]").value("Avaliacao"))
                .andExpect(jsonPath("$.paths['/auth/login'].post.security").isEmpty())
                .andExpect(jsonPath("$.paths['/auth/register'].post.security").isEmpty())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }

    @Test
    void swaggerUiContinuaPublico() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
