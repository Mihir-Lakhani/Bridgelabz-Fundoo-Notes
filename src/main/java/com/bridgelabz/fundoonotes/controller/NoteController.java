package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.dto.NoteRequest;
import com.bridgelabz.fundoonotes.dto.NoteResponse;
import com.bridgelabz.fundoonotes.service.NoteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, jwt.getSubject()));
    }

    @GetMapping
    public List<NoteResponse> list(@RequestParam(required = false) String search,
                                   @RequestParam(required = false) String label,
                                   @AuthenticationPrincipal Jwt jwt) {
        return service.list(jwt.getSubject(), search, label);
    }

    @GetMapping("/trash")
    public List<NoteResponse> trash(@AuthenticationPrincipal Jwt jwt) {
        return service.trash(jwt.getSubject());
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.get(id, jwt.getSubject());
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id, @Valid @RequestBody NoteRequest request,
                               @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, request, jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/{state}")
    public NoteResponse state(@PathVariable Long id, @PathVariable String state,
                              @RequestBody StateRequest request, @AuthenticationPrincipal Jwt jwt) {
        return service.setState(id, state, request.enabled(), jwt.getSubject());
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deletePermanently(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.deletePermanently(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    public record StateRequest(boolean enabled) { }

    @PutMapping("/{id}/labels/{labelId}")
    public NoteResponse addLabel(@PathVariable Long id, @PathVariable Long labelId,
                                 @AuthenticationPrincipal Jwt jwt) {
        return service.addLabel(id, labelId, jwt.getSubject());
    }

    @DeleteMapping("/{id}/labels/{labelId}")
    public NoteResponse removeLabel(@PathVariable Long id, @PathVariable Long labelId,
                                    @AuthenticationPrincipal Jwt jwt) {
        return service.removeLabel(id, labelId, jwt.getSubject());
    }
}
