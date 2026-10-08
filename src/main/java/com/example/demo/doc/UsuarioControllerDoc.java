package com.example.demo.doc;

import com.example.demo.dto.request.UpdateUsuarioRequest;
import com.example.demo.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Usuario", description = "CRUD de usuários (criar conta e logar ficam em Auth)")
public interface UsuarioControllerDoc {

    @Operation(summary = "Lista todos os usuários", description = "Devolve todos os usuários cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)))
    })
    ResponseEntity<List<UsuarioResponse>> listar();

    @Operation(summary = "Busca um usuário pelo id", description = "Devolve os dados de um usuário específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UsuarioResponse> buscarPorId(Long id);

    @Operation(summary = "Atualiza um usuário",
            description = "Atualiza nome e e-mail; a senha só é alterada se for enviada no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado para outro usuário")
    })
    ResponseEntity<UsuarioResponse> atualizar(Long id, UpdateUsuarioRequest request);

    @Operation(summary = "Remove um usuário", description = "Exclui o usuário pelo id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<Void> deletar(Long id);
}
