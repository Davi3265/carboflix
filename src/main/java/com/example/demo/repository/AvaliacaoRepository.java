package com.example.demo.repository;

import com.example.demo.entity.Avaliacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    boolean existsByPerfilIdAndFilmeId(Long perfilId, Long filmeId);

    List<Avaliacao> findByFilmeId(Long filmeId);

    List<Avaliacao> findByPerfilId(Long perfilId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.filme.id = :filmeId")
    Double mediaNotasPorFilme(@Param("filmeId") Long filmeId);

    long countByFilmeId(Long filmeId);
}
