package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.CtaRequest;
import br.com.infodive.infodive_api.dto.response.CtaResponse;
import br.com.infodive.infodive_api.service.CtaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ctas")
@RequiredArgsConstructor
public class CtaController {

    private final CtaService ctaService;

    @GetMapping("/{pagina}")
    public ResponseEntity<CtaResponse> findByPagina(@PathVariable String pagina) {
        return ResponseEntity.ok(ctaService.findByPagina(pagina));
    }

    @PutMapping("/{pagina}")
    public ResponseEntity<CtaResponse> update(
            @PathVariable String pagina, @Valid @RequestBody CtaRequest request) {
        return ResponseEntity.ok(ctaService.update(pagina, request));
    }
}
