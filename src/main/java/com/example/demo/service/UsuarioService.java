package com.example.demo.service;

import com.example.demo.dto.request.RegisterUserRequest;
import com.example.demo.dto.request.UpdateUsuarioRequest;
import com.example.demo.entity.Usuario;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.RegraNegocioException;
import com.example.demo.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio de {@code Usuario}. O cadastro (usado pelo
 * {@code AuthController}) também mora aqui — assim a checagem de e-mail
 * duplicado e a criação da conta ficam num único lugar, em vez de
 * duplicadas entre o Controller de autenticação e o Controller de
 * usuários.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegisterUserRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("E-mail já cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.nome());
        novoUsuario.setEmail(request.email());
        novoUsuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        return usuarioRepository.save(novoUsuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarDaConta(Usuario usuarioAutenticado) {
        return List.of(buscarPorId(usuarioAutenticado, usuarioAutenticado.getId()));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Usuario usuarioAutenticado, Long id) {
        if (!id.equals(usuarioAutenticado.getId())) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional
    public Usuario atualizar(Usuario usuarioAutenticado, Long id, UpdateUsuarioRequest request) {
        Usuario usuario = buscarPorId(usuarioAutenticado, id);

        usuarioRepository.findByEmail(request.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraNegocioException("E-mail já cadastrado para outro usuário.");
                });

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        if (request.senha() != null) {
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Usuario usuarioAutenticado, Long id) {
        Usuario usuario = buscarPorId(usuarioAutenticado, id);
        usuarioRepository.delete(usuario);
    }
}
