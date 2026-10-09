package com.example.demo.dto.response;

import java.util.Set;

/**
 * {@code mediaAvaliacoes} é {@code null} quando o filme ainda não tem
 * nenhuma avaliação (para não confundir com nota média 0); nesse caso
 * {@code totalAvaliacoes} vem 0.
 */
public record FilmeResponse(
        Long id,
        String titulo,
        String sinopse,
        Integer anoLancamento,
        Integer duracaoMin,
        String classificacaoIndicativa,
        String posterUrl,
        Set<CategoriaResponse> categorias,
        Double mediaAvaliacoes,
        long totalAvaliacoes) {
}
