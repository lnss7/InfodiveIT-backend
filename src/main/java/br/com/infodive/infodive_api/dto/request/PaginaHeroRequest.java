package br.com.infodive.infodive_api.dto.request;

public record PaginaHeroRequest(
        String eyebrow,
        String headline,
        String subtitulo,
        String tagline
) {}
