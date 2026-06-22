package br.com.infodive.infodive_api.repository;

import br.com.infodive.infodive_api.entity.Solucao;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolucaoRepository extends JpaRepository<Solucao, UUID> {

    List<Solucao> findAllByAtivoTrueOrderByOrdemAscTituloAsc();

    Optional<Solucao> findBySlugAndAtivoTrue(String slug);
}
