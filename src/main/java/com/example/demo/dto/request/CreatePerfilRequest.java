package com.example.demo.dto.request;

import com.example.demo.entity.enums.TipoPerfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Não tem {@code usuarioId}: o perfil sempre é criado para a conta
 * autenticada (extraída do token JWT no Controller), nunca para a conta
 * que o cliente disser.
 */
public record CreatePerfilRequest(

        @NotBlank(message = "Nome do perfil é obrigatório.")
        @Size(max = 120, message = "Nome do perfil deve ter no máximo 120 caracteres.")
        String nomePerfil,

        @Size(max = 500, message = "URL do avatar deve ter no máximo 500 caracteres.")
        String avatarUrl,

        @NotNull(message = "Tipo do perfil é obrigatório (ADULTO ou INFANTIL).")
        TipoPerfil tipo) {
}
