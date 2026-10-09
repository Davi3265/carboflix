package com.example.demo.repository;

import com.example.demo.entity.Perfil;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    List<Perfil> findByUsuarioId(Long usuarioId);

    Optional<Perfil> findByIdAndUsuarioId(Long id, Long usuarioId);

    long countByUsuarioId(Long usuarioId);
}
