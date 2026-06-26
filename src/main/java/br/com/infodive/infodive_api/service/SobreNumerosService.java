package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.SobreNumerosRequest;
import br.com.infodive.infodive_api.dto.response.SobreNumerosResponse;
import br.com.infodive.infodive_api.entity.SobreNumeros;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.SobreNumerosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SobreNumerosService {

    private final SobreNumerosRepository repository;

    @Transactional(readOnly = true)
    public SobreNumerosResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Números (sobre) não encontrados"));
    }

    @Transactional
    public SobreNumerosResponse update(SobreNumerosRequest request) {
        SobreNumeros entity = repository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Números (sobre) não encontrados"));
        entity.setTextoDescritivo(request.textoDescritivo());
        entity.setStats(request.stats());
        return toResponse(repository.save(entity));
    }

    private SobreNumerosResponse toResponse(SobreNumeros e) {
        return new SobreNumerosResponse(e.getTextoDescritivo(), e.getStats());
    }
}
