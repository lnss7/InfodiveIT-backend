package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.HomeSegurancaMarqueeRequest;
import br.com.infodive.infodive_api.dto.response.HomeSegurancaMarqueeResponse;
import br.com.infodive.infodive_api.service.HomeSegurancaMarqueeService;
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

@RestController
@RequestMapping("/home-seguranca-marquee")
@RequiredArgsConstructor
public class HomeSegurancaMarqueeController {

    private final HomeSegurancaMarqueeService service;

    @GetMapping
    public ResponseEntity<List<HomeSegurancaMarqueeResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<HomeSegurancaMarqueeResponse> create(@Valid @RequestBody HomeSegurancaMarqueeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeSegurancaMarqueeResponse> update(
            @PathVariable UUID id, @Valid @RequestBody HomeSegurancaMarqueeRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
