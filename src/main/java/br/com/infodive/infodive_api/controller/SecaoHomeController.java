package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.SecaoHomeRequest;
import br.com.infodive.infodive_api.dto.response.SecaoHomeResponse;
import br.com.infodive.infodive_api.service.SecaoHomeService;
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
@RequestMapping("/secoes-home")
@RequiredArgsConstructor
public class SecaoHomeController {

    private final SecaoHomeService secaoHomeService;

    @GetMapping("/{secao}")
    public ResponseEntity<SecaoHomeResponse> findBySecao(@PathVariable String secao) {
        return ResponseEntity.ok(secaoHomeService.findBySecao(secao));
    }

    @PutMapping("/{secao}")
    public ResponseEntity<SecaoHomeResponse> update(
            @PathVariable String secao, @Valid @RequestBody SecaoHomeRequest request) {
        return ResponseEntity.ok(secaoHomeService.update(secao, request));
    }
}
