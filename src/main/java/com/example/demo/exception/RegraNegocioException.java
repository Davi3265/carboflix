package com.example.demo.exception;

/**
 * Lançada pelos Services quando a requisição é válida (passou pelas
 * validações de {@code @Valid} do DTO), mas viola uma regra de negócio:
 * e-mail já cadastrado, limite de perfis por conta atingido, perfil que já
 * avaliou o mesmo filme, etc. Mapeada para 409 (Conflict) pelo
 * {@link GlobalExceptionHandler}.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
