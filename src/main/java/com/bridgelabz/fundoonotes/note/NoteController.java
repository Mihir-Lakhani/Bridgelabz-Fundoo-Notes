package com.bridgelabz.fundoonotes.note;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService service;
    public NoteController(NoteService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, jwt.getSubject()));
    }

    @GetMapping
    public List<NoteResponse> list(@RequestParam(required = false) String search,
                                   @RequestParam(required = false) String label,
                                   @RequestParam(required = false) Boolean archived,
                                   @AuthenticationPrincipal Jwt jwt) {
        return service.list(jwt.getSubject(), search, label, archived);
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

    @PatchMapping("/{id}/{state}")
    public NoteResponse state(@PathVariable Long id, @PathVariable String state,
                              @RequestBody StateRequest request, @AuthenticationPrincipal Jwt jwt) {
        return service.setState(id, state, request.enabled(), jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    public record StateRequest(boolean enabled) { }
}
