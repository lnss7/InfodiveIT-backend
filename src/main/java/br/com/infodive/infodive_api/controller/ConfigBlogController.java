package br.com.infodive.infodive_api.controller;

import br.com.infodive.infodive_api.dto.request.ConfigBlogRequest;
import br.com.infodive.infodive_api.dto.response.ConfigBlogResponse;
import br.com.infodive.infodive_api.service.ConfigBlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config-blog")
@RequiredArgsConstructor
public class ConfigBlogController {

    private final ConfigBlogService configBlogService;

    @GetMapping
    public ResponseEntity<ConfigBlogResponse> get() {
        return ResponseEntity.ok(configBlogService.get());
    }

    @PutMapping
    public ResponseEntity<ConfigBlogResponse> update(@Valid @RequestBody ConfigBlogRequest request) {
        return ResponseEntity.ok(configBlogService.update(request));
    }
}
