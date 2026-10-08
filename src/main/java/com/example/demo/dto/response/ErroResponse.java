package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * Formato padrão de erro devolvido por toda a API (ver
 * {@code GlobalExceptionHandler}), exceto quando há campos inválidos de
 * DTO — nesse caso quem é devolvido é {@link ErroValidacaoResponse}.
 */
public record ErroResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp) {

    public ErroResponse(int status, String mensagem) {
        this(status, mensagem, LocalDateTime.now());
    }
}
