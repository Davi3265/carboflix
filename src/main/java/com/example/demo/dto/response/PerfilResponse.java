package com.example.demo.dto.response;

import com.example.demo.entity.enums.TipoPerfil;
import java.time.LocalDateTime;

public record PerfilResponse(
        Long id,
        Long usuarioId,
        String nomePerfil,
        String avatarUrl,
        TipoPerfil tipo,
        LocalDateTime criadoEm) {
}
