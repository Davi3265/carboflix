package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoriaRequest(

        @NotBlank(message = "Nome da categoria é obrigatório.")
        @Size(max = 120, message = "Nome da categoria deve ter no máximo 120 caracteres.")
        String nome,

        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres.")
        String descricao) {
}
