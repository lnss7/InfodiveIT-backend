package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.ConfigBlogRequest;
import br.com.infodive.infodive_api.dto.response.ConfigBlogResponse;
import br.com.infodive.infodive_api.entity.ConfigBlog;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.repository.ConfigBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigBlogService {

    private final ConfigBlogRepository configBlogRepository;

    @Transactional(readOnly = true)
    public ConfigBlogResponse get() {
        return configBlogRepository.findAll().stream().findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração de blog não encontrada"));
    }

    @Transactional
    public ConfigBlogResponse update(ConfigBlogRequest request) {
        ConfigBlog entity = configBlogRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Configuração de blog não encontrada"));
        entity.setArtigosEyebrow(request.artigosEyebrow());
        entity.setArtigosHeadline(request.artigosHeadline());
        entity.setSocialEyebrow(request.socialEyebrow());
        entity.setSocialHeadline(request.socialHeadline());
        entity.setSocialDescricao(request.socialDescricao());
        entity.setUrlInstagram(request.urlInstagram());
        entity.setUrlLinkedin(request.urlLinkedin());
        return toResponse(configBlogRepository.save(entity));
    }

    private ConfigBlogResponse toResponse(ConfigBlog e) {
        return new ConfigBlogResponse(
                e.getArtigosEyebrow(), e.getArtigosHeadline(), e.getSocialEyebrow(), e.getSocialHeadline(),
                e.getSocialDescricao(), e.getUrlInstagram(), e.getUrlLinkedin());
    }
}
