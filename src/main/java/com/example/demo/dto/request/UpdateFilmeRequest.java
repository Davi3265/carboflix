package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateFilmeRequest(

        @NotBlank(message = "Título é obrigatório.")
        @Size(max = 200, message = "Título deve ter no máximo 200 caracteres.")
        String titulo,

        String sinopse,

        @NotNull(message = "Ano de lançamento é obrigatório.")
        @Min(value = 1888, message = "Ano de lançamento inválido.")
        @Max(value = 2100, message = "Ano de lançamento inválido.")
        Integer anoLancamento,

        @NotNull(message = "Duração (em minutos) é obrigatória.")
        @Min(value = 1, message = "Duração deve ser maior que zero.")
        Integer duracaoMin,

        @NotBlank(message = "Classificação indicativa é obrigatória.")
        @Size(max = 20, message = "Classificação indicativa deve ter no máximo 20 caracteres.")
        String classificacaoIndicativa,

        @Size(max = 500, message = "URL do poster deve ter no máximo 500 caracteres.")
        String posterUrl,

        Set<@NotNull(message = "categoriaIds não pode conter valores nulos.") Long> categoriaIds) {
}
