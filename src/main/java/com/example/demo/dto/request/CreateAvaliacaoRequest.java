package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * {@code perfilId} identifica qual perfil da conta autenticada está
 * avaliando — o Service confirma que esse perfil pertence a quem está
 * logado antes de salvar, então não é possível avaliar "em nome" do
 * perfil de outra conta.
 */
public record CreateAvaliacaoRequest(

        @NotNull(message = "Perfil é obrigatório.")
        Long perfilId,

        @NotNull(message = "Filme é obrigatório.")
        Long filmeId,

        @NotNull(message = "Nota é obrigatória.")
        @Min(value = 1, message = "Nota deve ser entre 1 e 5.")
        @Max(value = 5, message = "Nota deve ser entre 1 e 5.")
        Integer nota,

        String comentario) {
}
