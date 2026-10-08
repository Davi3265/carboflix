package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * Representação pública de um {@code Usuario}: nunca inclui {@code senhaHash}.
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String role,
        LocalDateTime criadoEm) {
}
