package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Atualiza só a nota e o comentário — o perfil e o filme de uma avaliação
 * não mudam depois de criada (para mudar o filme avaliado, o caminho é
 * remover e criar uma nova avaliação).
 */
public record UpdateAvaliacaoRequest(

        @NotNull(message = "Nota é obrigatória.")
        @Min(value = 1, message = "Nota deve ser entre 1 e 5.")
        @Max(value = 5, message = "Nota deve ser entre 1 e 5.")
        Integer nota,

        String comentario) {
}
