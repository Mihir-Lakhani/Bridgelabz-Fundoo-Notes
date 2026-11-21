package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.dto.LabelRequest;
import com.bridgelabz.fundoonotes.dto.LabelResponse;
import com.bridgelabz.fundoonotes.service.LabelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService service;

    public LabelController(LabelService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<LabelResponse> create(@Valid @RequestBody LabelRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request.name(), jwt.getSubject()));
    }

    @GetMapping
    public List<LabelResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return service.list(jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
