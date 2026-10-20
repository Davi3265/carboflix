package com.example.demo.doc;

import com.example.demo.dto.request.CreateAvaliacaoRequest;
import com.example.demo.dto.request.UpdateAvaliacaoRequest;
import com.example.demo.dto.response.AvaliacaoResponse;
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

@Tag(name = "Avaliacao", description = "Leitura de avaliações e alterações restritas aos perfis da própria conta")
public interface AvaliacaoControllerDoc {

    @Operation(summary = "Lista as avaliações")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de registros",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AvaliacaoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ResponseEntity<List<AvaliacaoResponse>> listar();

    @Operation(summary = "Busca uma avaliação pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    ResponseEntity<AvaliacaoResponse> buscarPorId(Long id);

    @Operation(summary = "Cria uma avaliação de um perfil da própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<AvaliacaoResponse> criar(@Parameter(hidden = true) Usuario usuarioAutenticado, CreateAvaliacaoRequest request);

    @Operation(summary = "Atualiza uma avaliação de um perfil da própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<AvaliacaoResponse> atualizar(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id, UpdateAvaliacaoRequest request);

    @Operation(summary = "Remove uma avaliação de um perfil da própria conta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro removido"),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<Void> deletar(@Parameter(hidden = true) Usuario usuarioAutenticado, Long id);
}
