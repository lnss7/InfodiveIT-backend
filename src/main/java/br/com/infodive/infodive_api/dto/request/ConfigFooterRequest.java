package br.com.infodive.infodive_api.dto.request;

public record ConfigFooterRequest(
        String descricaoEmpresa,
        String badgeNoc,
        String badgeCloud,
        String nomeLegal,
        String urlLinkedin,
        String urlInstagram,
        String urlFacebook
) {}
