package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * {@code senha} é opcional: quando o campo for omitido (ou enviado como
 * {@code null}) no JSON, a senha atual do usuário não é alterada. Quando
 * enviada, precisa ter ao menos 6 caracteres, igual à validação do
 * cadastro — por isso uma string vazia é rejeitada em vez de interpretada
 * como "manter a senha".
 */
public record UpdateUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email,

        @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres.")
        String senha) {
}
