package com.example.demo.config;

import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Ponte entre o Spring Security e o nosso banco: dado o e-mail informado no
 * login, busca o {@link com.example.demo.entity.Usuario} correspondente
 * (que já implementa {@link UserDetails}).
 */
@Service
public class AuthConfig implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AuthConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}
