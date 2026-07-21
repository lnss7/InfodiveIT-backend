package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HomeSolucoesBentoRequest;
import br.com.infodive.infodive_api.dto.response.HomeSolucoesBentoResponse;
import br.com.infodive.infodive_api.entity.HomeSolucoesBento;
import br.com.infodive.infodive_api.entity.Solucao;
import br.com.infodive.infodive_api.exception.BusinessException;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HomeSolucoesBentoRepository;
import br.com.infodive.infodive_api.repository.SolucaoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeSolucoesBentoService {

    private final HomeSolucoesBentoRepository repository;
    private final SolucaoRepository solucaoRepository;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional(readOnly = true)
    public List<HomeSolucoesBentoResponse> findAll() {
        return repository.findAllByOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    public HomeSolucoesBentoResponse findById(UUID id) {
        HomeSolucoesBento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do bento não encontrado: " + id));
        return toResponse(entity);
    }

    @Transactional
    public HomeSolucoesBentoResponse create(HomeSolucoesBentoRequest request) {
        throw new BusinessException("Criação de novos itens no Bento Grid não é permitida. Apenas atualização dos 4 cartões existentes é autorizada.");
    }

    @Transactional
    public HomeSolucoesBentoResponse update(UUID id, HomeSolucoesBentoRequest request) {
        HomeSolucoesBento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do bento não encontrado: " + id));
        
        String oldImagemIaUrl = entity.getImagemIaUrl();

        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setIcone(request.icone());
        entity.setImagemIaUrl(request.imagemIaUrl());
        entity.setTextoCarrossel(request.textoCarrossel());
        entity.setOrdem(request.ordem());

        if (request.solucaoId() != null) {
            Solucao solucao = solucaoRepository.findById(request.solucaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Solução não encontrada: " + request.solucaoId()));
            entity.setSolucao(solucao);
        } else {
            entity.setSolucao(null);
        }

        if (oldImagemIaUrl != null && !oldImagemIaUrl.equals(request.imagemIaUrl())) {
            supabaseStorageService.deleteFile(oldImagemIaUrl);
        }

        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        throw new BusinessException("Exclusão de itens do Bento Grid não é permitida. Os 4 cartões são de estrutura fixa.");
    }

    private HomeSolucoesBentoResponse toResponse(HomeSolucoesBento e) {
        Solucao s = e.getSolucao();
        return new HomeSolucoesBentoResponse(
                e.getId(),
                e.getNome(),
                e.getDescricao(),
                e.getIcone(),
                e.getImagemIaUrl(),
                e.getTextoCarrossel(),
                e.getOrdem(),
                s != null ? s.getId() : null,
                s != null ? s.getSlug() : null,
                s != null ? s.getTitulo() : null
        );
    }
}
