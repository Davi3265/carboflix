package com.example.demo.doc;

import com.example.demo.dto.request.CreateCategoriaRequest;
import com.example.demo.dto.request.UpdateCategoriaRequest;
import com.example.demo.dto.response.CategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Categoria", description = "CRUD das categorias de filmes")
public interface CategoriaControllerDoc {

    @Operation(summary = "Lista as categorias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de registros",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ResponseEntity<List<CategoriaResponse>> listar();

    @Operation(summary = "Busca uma categoria pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    ResponseEntity<CategoriaResponse> buscarPorId(Long id);

    @Operation(summary = "Cria uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<CategoriaResponse> criar(CreateCategoriaRequest request);

    @Operation(summary = "Atualiza uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<CategoriaResponse> atualizar(Long id, UpdateCategoriaRequest request);

    @Operation(summary = "Remove uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro removido"),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<Void> deletar(Long id);
}
