package com.example.demo.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.TokenConfig;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Roda uma vez por requisição, antes do filtro padrão de login do Spring
 * Security. Lê o header {@code Authorization: Bearer <token>}, valida o JWT
 * (via {@link TokenConfig}) e, se for válido, autentica o usuário no
 * {@link SecurityContextHolder} para a requisição atual — é isso que faz
 * {@code anyRequest().authenticated()} funcionar sem sessão (STATELESS).
 * Sem esse filtro, nenhum token seria conferido e toda rota protegida
 * sempre retornaria 401, mesmo com um token válido.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(TokenConfig tokenConfig, UsuarioRepository usuarioRepository) {
        this.tokenConfig = tokenConfig;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            DecodedJWT decodedJWT = tokenConfig.validateToken(token);

            if (decodedJWT != null) {
                String email = tokenConfig.getSubject(decodedJWT);
                Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

                if (usuario.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            usuario.get(), null, usuario.get().getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
