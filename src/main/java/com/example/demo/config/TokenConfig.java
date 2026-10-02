package com.example.demo.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.Usuario;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gera e valida os tokens JWT usados para autenticar as requisições do
 * CarboFlix. O token carrega o e-mail do usuário como subject (usado pelo
 * {@link com.example.demo.security.JwtAuthenticationFilter} para localizar o
 * usuário autenticado em cada requisição) e o id como claim extra.
 */
@Component
public class TokenConfig {

    private static final long EXPIRATION_SECONDS = 86_400; // 24 horas

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userId", usuario.getId())
                .withSubject(usuario.getEmail())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(EXPIRATION_SECONDS))
                .sign(algorithm);
    }

    /**
     * Valida a assinatura e a expiração do token. Retorna o token decodificado
     * quando válido, ou {@code null} quando inválido/expirado/alterado.
     */
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String getSubject(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }
}
