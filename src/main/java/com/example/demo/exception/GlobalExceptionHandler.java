package com.example.demo.exception;

import com.example.demo.dto.response.ErroResponse;
import com.example.demo.dto.response.ErroValidacaoResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centraliza o tratamento de erro de toda a API. Antes desta classe, só o
 * {@code AuthController} tratava algo (credenciais inválidas no login);
 * agora qualquer Controller — Usuario, e os que o resto do grupo for
 * fechando (Perfil, Filme, Categoria, Avaliacao) — tem o mesmo formato de
 * resposta de erro sem precisar de um {@code @ExceptionHandler} próprio.
 *
 * <p>Mapeamento usado:
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} (falha de {@code @Valid} num DTO) → 400,
 *       com a lista de campos inválidos</li>
 *   <li>{@link HttpMessageNotReadableException} (JSON malformado / enum inválido) → 400</li>
 *   <li>{@link RecursoNaoEncontradoException} → 404</li>
 *   <li>{@link RegraNegocioException} (regra de negócio violada) → 409</li>
 *   <li>{@link DataIntegrityViolationException} (ex.: excluir um registro que
 *       ainda tem outro vinculado) → 409</li>
 *   <li>{@link BadCredentialsException} (login com e-mail/senha errados) → 401</li>
 *   <li>qualquer outra {@link Exception} não prevista → 500, sem expor detalhes internos</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Valor inválido para o parâmetro '" + ex.getName() + "'."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacaoResponse> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }

        ErroValidacaoResponse body = new ErroValidacaoResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos. Verifique os campos informados.",
                campos);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        ErroResponse body = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Corpo da requisição ausente, malformado ou com um valor inválido para algum campo.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroResponse body = new ErroResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraNegocio(RegraNegocioException ex) {
        ErroResponse body = new ErroResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(DataIntegrityViolationException ex) {
        ErroResponse body = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Não é possível completar a operação: o registro está vinculado a outros dados.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(BadCredentialsException ex) {
        ErroResponse body = new ErroResponse(HttpStatus.UNAUTHORIZED.value(), "E-mail ou senha inválidos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex) {
        ErroResponse body = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor. Tente novamente mais tarde.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
