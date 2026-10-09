package com.example.demo.doc;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterUserRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.RegisterUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Documentação Swagger do {@code AuthController}, separada da classe do
 * Controller (mesmo padrão do pacote {@code doc} visto em aula): o
 * Controller implementa esta interface e herda as anotações
 * {@code @Operation}/{@code @ApiResponses}; só o {@code @Tag} fica na
 * interface, já que ele descreve o grupo de endpoints como um todo.
 */
@Tag(name = "Auth", description = "Cadastro e autenticação de usuários")
public interface AuthControllerDoc {

    @SecurityRequirements
    @Operation(
            summary = "Cadastra um novo usuário",
            description = "Cria a conta com nome, e-mail e senha. A senha é criptografada com BCrypt antes de salvar."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterUserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    ResponseEntity<RegisterUserResponse> register(RegisterUserRequest request);

    @SecurityRequirements
    @Operation(
            summary = "Autentica um usuário",
            description = "Valida e-mail e senha e devolve um token JWT válido por 24h."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    })
    ResponseEntity<LoginResponse> login(LoginRequest request);
}
