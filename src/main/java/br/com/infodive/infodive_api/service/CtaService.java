package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.CtaRequest;
import br.com.infodive.infodive_api.dto.response.CtaResponse;
import br.com.infodive.infodive_api.entity.Cta;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.CtaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CtaService {

    private final CtaRepository ctaRepository;

    @Transactional(readOnly = true)
    public CtaResponse findByPagina(String pagina) {
        return ctaRepository.findByPagina(pagina)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para a página: " + pagina));
    }

    @Transactional
    public CtaResponse update(String pagina, CtaRequest request) {
        Cta entity = ctaRepository.findByPagina(pagina)
                .orElseThrow(() -> new ResourceNotFoundException("CTA não encontrado para a página: " + pagina));
        entity.setTitulo(request.titulo());
        entity.setSubtitulo(request.subtitulo());
        entity.setCtaTexto(request.ctaTexto());
        return toResponse(ctaRepository.save(entity));
    }

    private CtaResponse toResponse(Cta e) {
        return new CtaResponse(e.getPagina(), e.getTitulo(), e.getSubtitulo(), e.getCtaTexto());
    }
}
