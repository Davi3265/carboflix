package com.example.demo.exception;

/**
 * Lançada pelos Services quando um registro buscado por id (ou por alguma
 * outra chave) não existe — ou, no caso de recursos que pertencem a um
 * usuário (perfil, avaliação), quando existe mas não pertence a quem está
 * autenticado. Nos dois casos devolvemos 404 (ver {@link GlobalExceptionHandler}):
 * não existir e "não ser seu" são tratados da mesma forma para não revelar
 * a existência de registros de outras contas.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
