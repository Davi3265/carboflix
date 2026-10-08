package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Long id,
        Long perfilId,
        String nomePerfil,
        Long filmeId,
        String tituloFilme,
        Integer nota,
        String comentario,
        LocalDateTime criadoEm) {
}
