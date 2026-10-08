package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Devolvido quando {@code @Valid} rejeita o DTO de entrada
 * ({@code MethodArgumentNotValidException}). {@code campos} mapeia cada
 * campo inválido para a mensagem da anotação que falhou (a mesma mensagem
 * declarada em {@code @NotBlank}, {@code @Size}, etc. no DTO).
 */
public record ErroValidacaoResponse(
        int status,
        String mensagem,
        Map<String, String> campos,
        LocalDateTime timestamp) {

    public ErroValidacaoResponse(int status, String mensagem, Map<String, String> campos) {
        this(status, mensagem, campos, LocalDateTime.now());
    }
}
