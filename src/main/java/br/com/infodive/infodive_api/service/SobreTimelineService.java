package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.SobreTimelineRequest;
import br.com.infodive.infodive_api.dto.response.SobreTimelineResponse;
import br.com.infodive.infodive_api.entity.SobreTimeline;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.SobreTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SobreTimelineService {

    private final SobreTimelineRepository repository;

    @Transactional(readOnly = true)
    public SobreTimelineResponse get() {
        return repository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Timeline (sobre) não encontrada"));
    }

    @Transactional
    public SobreTimelineResponse update(SobreTimelineRequest request) {
        SobreTimeline entity = repository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Timeline (sobre) não encontrada"));
        entity.setEyebrow(request.eyebrow());
        entity.setHeadline(request.headline());
        entity.setMarcos(request.marcos());
        return toResponse(repository.save(entity));
    }

    private SobreTimelineResponse toResponse(SobreTimeline e) {
        return new SobreTimelineResponse(e.getEyebrow(), e.getHeadline(), e.getMarcos());
    }
}
