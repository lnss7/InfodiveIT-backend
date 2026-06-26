package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.SobreTimelineRequest;
import br.com.infodive.infodive_api.dto.response.SobreTimelineResponse;
import br.com.infodive.infodive_api.service.SobreTimelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sobre-timeline")
@RequiredArgsConstructor
public class SobreTimelineController {

    private final SobreTimelineService service;

    @GetMapping
    public ResponseEntity<SobreTimelineResponse> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    public ResponseEntity<SobreTimelineResponse> update(@Valid @RequestBody SobreTimelineRequest request) {
        return ResponseEntity.ok(service.update(request));
    }
}
