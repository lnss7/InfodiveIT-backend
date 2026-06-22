package br.com.infodive.infodive_api.mapper;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.entity.Solucao;
import org.springframework.stereotype.Component;

@Component
public class SolucaoMapper {

    public SolucaoResponse toResponse(Solucao entity) {
        return new SolucaoResponse(
                entity.getId(),
                entity.getTitulo(),       // frontend espera "nome"
                entity.getSlug(),
                entity.getIcone(),
                entity.getDescricaoCurta(),
                entity.getOverview(),     // frontend espera "descricaoCompleta"
                entity.getOrdem(),
                entity.isAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Solucao toEntity(SolucaoRequest request) {
        return Solucao.builder()
                .slug(request.slug())
                .titulo(request.titulo())
                .icone(request.icone())
                .subtituloCurto(request.subtituloCurto())
                .descricaoCurta(request.descricaoCurta())
                .overview(request.overview())
                .features(request.features())
                .imagemUrl(request.imagemUrl())
                .fabricantesTitulo(request.fabricantesTitulo())
                .fabricantesDescricao(request.fabricantesDescricao())
                .ordem(request.ordem())
                .build();
    }

    /**
     * Atualiza os campos escalares. O slug é imutável; as associações de fabricantes
     * são resolvidas no service (precisa do FabricanteRepository).
     */
    public void updateEntity(Solucao entity, SolucaoRequest request) {
        entity.setTitulo(request.titulo());
        entity.setIcone(request.icone());
        entity.setSubtituloCurto(request.subtituloCurto());
        entity.setDescricaoCurta(request.descricaoCurta());
        entity.setOverview(request.overview());
        entity.setFeatures(request.features());
        entity.setImagemUrl(request.imagemUrl());
        entity.setFabricantesTitulo(request.fabricantesTitulo());
        entity.setFabricantesDescricao(request.fabricantesDescricao());
        entity.setOrdem(request.ordem());
    }
}
