package com.example.demo.doc;

import com.example.demo.dto.request.UpdateUsuarioRequest;
import com.example.demo.dto.response.UsuarioResponse;
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

@Tag(name = "Usuario", description = "CRUD da própria conta autenticada")
public interface UsuarioControllerDoc {

    @Operation(summary = "Lista somente a própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados da conta autenticada",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ResponseEntity<List<UsuarioResponse>> listar(@Parameter(hidden = true) Usuario usuarioAutenticado);

    @Operation(summary = "Busca os dados da própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Conta ausente ou não pertence ao usuário autenticado")
    })
    ResponseEntity<UsuarioResponse> buscarPorId(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id);

    @Operation(summary = "Atualiza nome, e-mail e senha da própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Conta ausente ou não pertence ao usuário autenticado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<UsuarioResponse> atualizar(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id, UpdateUsuarioRequest request);

    @Operation(summary = "Remove a própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta removida"),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Conta ausente ou não pertence ao usuário autenticado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<Void> deletar(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id);
}
