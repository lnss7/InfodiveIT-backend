package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.HomeSolucoesBentoRequest;
import br.com.infodive.infodive_api.dto.response.HomeSolucoesBentoResponse;
import br.com.infodive.infodive_api.entity.HomeSolucoesBento;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.HomeSolucoesBentoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeSolucoesBentoService {

    private final HomeSolucoesBentoRepository repository;

    @Transactional(readOnly = true)
    public List<HomeSolucoesBentoResponse> findAll() {
        return repository.findAllByOrderByOrdemAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public HomeSolucoesBentoResponse create(HomeSolucoesBentoRequest request) {
        HomeSolucoesBento entity = HomeSolucoesBento.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .icone(request.icone())
                .imagemIaUrl(request.imagemIaUrl())
                .ordem(request.ordem())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public HomeSolucoesBentoResponse update(UUID id, HomeSolucoesBentoRequest request) {
        HomeSolucoesBento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do bento não encontrado: " + id));
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        entity.setIcone(request.icone());
        entity.setImagemIaUrl(request.imagemIaUrl());
        entity.setOrdem(request.ordem());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item do bento não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private HomeSolucoesBentoResponse toResponse(HomeSolucoesBento e) {
        return new HomeSolucoesBentoResponse(
                e.getId(), e.getNome(), e.getDescricao(), e.getIcone(), e.getImagemIaUrl(), e.getOrdem());
    }
}
