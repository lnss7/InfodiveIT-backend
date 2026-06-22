package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.SolucaoRequest;
import br.com.infodive.infodive_api.dto.response.SolucaoResponse;
import br.com.infodive.infodive_api.service.SolucaoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * O frontend consome soluções como "categorias" (src/lib/api.ts), por isso o endpoint
 * é /categorias mesmo a tabela/entidade sendo Solucao.
 */
@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final SolucaoService solucaoService;

    @GetMapping
    public ResponseEntity<List<SolucaoResponse>> findAll() {
        return ResponseEntity.ok(solucaoService.findAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SolucaoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(solucaoService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<SolucaoResponse> create(@Valid @RequestBody SolucaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solucaoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolucaoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SolucaoRequest request) {
        return ResponseEntity.ok(solucaoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        solucaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
