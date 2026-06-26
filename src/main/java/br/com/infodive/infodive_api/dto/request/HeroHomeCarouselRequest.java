package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HeroHomeCarouselRequest(
        @NotBlank String imagemUrl,
        int ordem
) {}
