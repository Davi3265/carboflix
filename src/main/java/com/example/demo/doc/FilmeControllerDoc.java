package com.example.demo.doc;

import com.example.demo.dto.request.CreateFilmeRequest;
import com.example.demo.dto.request.UpdateFilmeRequest;
import com.example.demo.dto.response.FilmeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Filme", description = "CRUD dos filmes e associação com categorias")
public interface FilmeControllerDoc {

    @Operation(summary = "Lista filmes com categorias e média de avaliações")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de registros",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FilmeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ResponseEntity<List<FilmeResponse>> listar();

    @Operation(summary = "Busca um filme pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilmeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    ResponseEntity<FilmeResponse> buscarPorId(Long id);

    @Operation(summary = "Cria um filme")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilmeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<FilmeResponse> criar(CreateFilmeRequest request);

    @Operation(summary = "Atualiza um filme")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilmeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<FilmeResponse> atualizar(Long id, UpdateFilmeRequest request);

    @Operation(summary = "Remove um filme")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro removido"),
            @ApiResponse(responseCode = "400", description = "Dados ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Operação impede a integridade dos dados ou viola uma regra de negócio")
    })
    ResponseEntity<Void> deletar(Long id);
}
