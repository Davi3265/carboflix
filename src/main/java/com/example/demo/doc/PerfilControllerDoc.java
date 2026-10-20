package com.example.demo.doc;

import com.example.demo.dto.request.CreatePerfilRequest;
import com.example.demo.dto.request.UpdatePerfilRequest;
import com.example.demo.dto.response.PerfilResponse;
import com.example.demo.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Perfil", description = "CRUD dos perfis da conta autenticada")
public interface PerfilControllerDoc {

    @Operation(summary = "Lista os perfis da conta autenticada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfis da conta",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PerfilResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ResponseEntity<List<PerfilResponse>> listar(@Parameter(hidden = true) Usuario usuarioAutenticado);

    @Operation(summary = "Busca um perfil pelo id",
            description = "Devolve somente um perfil que pertence à conta autenticada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PerfilResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado na conta")
    })
    ResponseEntity<PerfilResponse> buscarPorId(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id);

    @Operation(summary = "Cria um perfil",
            description = "Cria o perfil para o usuário do token JWT. Não recebe usuarioId no corpo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PerfilResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "409", description = "Limite de perfis por conta atingido")
    })
    ResponseEntity<PerfilResponse> criar(@Parameter(hidden = true) Usuario usuarioAutenticado,
            CreatePerfilRequest request);

    @Operation(summary = "Atualiza um perfil",
            description = "Atualiza nome, avatar e tipo de um perfil da conta autenticada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PerfilResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado na conta")
    })
    ResponseEntity<PerfilResponse> atualizar(@Parameter(hidden = true) Usuario usuarioAutenticado,
            Long id, UpdatePerfilRequest request);

    @Operation(summary = "Remove um perfil",
            description = "Exclui um perfil da conta autenticada. Avaliações vinculadas podem impedir a exclusão.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil removido"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado na conta"),
            @ApiResponse(responseCode = "409", description = "Perfil vinculado a outros dados")
    })
    ResponseEntity<Void> deletar(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id);
}
