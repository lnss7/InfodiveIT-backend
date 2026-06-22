package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Fabricante;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FabricanteRepository extends JpaRepository<Fabricante, UUID> {

    // Listagem pública com filtro opcional de destaque — usado em GET /fabricantes(?destaque=true)
    @Query("""
            SELECT f FROM Fabricante f
            WHERE f.ativo = true
            AND (:destaque IS NULL OR f.destaque = :destaque)
            ORDER BY f.ordem ASC, f.nome ASC
            """)
    List<Fabricante> findAllWithFilters(@Param("destaque") Boolean destaque);

    Optional<Fabricante> findBySlugAndAtivoTrue(String slug);
}
