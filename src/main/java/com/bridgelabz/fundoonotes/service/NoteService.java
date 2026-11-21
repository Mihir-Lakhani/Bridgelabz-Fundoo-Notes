package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.NoteRequest;
import com.bridgelabz.fundoonotes.dto.NoteResponse;
import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteRepository notes;
    private final UserRepository users;

    public NoteService(NoteRepository notes, UserRepository users) {
        this.notes = notes;
        this.users = users;
    }

    private AppUser owner(String email) {
        return users.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private Note ownedNote(Long id, String email) {
        return notes.findByIdAndOwnerId(id, owner(email).getId())
                .orElseThrow(() -> new NoSuchElementException("Note not found"));
    }

    public NoteResponse create(NoteRequest request, String email) {
        Note note = new Note(request.title(), request.description(), owner(email));
        return NoteResponse.from(notes.save(note));
    }

    public List<NoteResponse> list(String email, String search) {
        return notes.findByOwnerId(owner(email).getId()).stream()
                .filter(note -> !note.isTrashed())
                .filter(note -> search == null || search.isBlank()
                        || note.getTitle().toLowerCase().contains(search.toLowerCase())
                        || (note.getDescription() != null
                        && note.getDescription().toLowerCase().contains(search.toLowerCase())))
                .map(NoteResponse::from).toList();
    }

    public NoteResponse get(Long id, String email) {
        Note note = ownedNote(id, email);
        if (note.isTrashed()) throw new NoSuchElementException("Note not found");
        return NoteResponse.from(note);
    }

    public NoteResponse update(Long id, NoteRequest request, String email) {
        Note note = ownedNote(id, email);
        if (note.isTrashed()) throw new NoSuchElementException("Note not found");
        note.setTitle(request.title());
        note.setDescription(request.description());
        return NoteResponse.from(notes.save(note));
    }

    public void delete(Long id, String email) {
        setState(id, "trash", true, email);
    }

    public List<NoteResponse> trash(String email) {
        return notes.findByOwnerId(owner(email).getId()).stream()
                .filter(Note::isTrashed).map(NoteResponse::from).toList();
    }

    public NoteResponse setState(Long id, String state, boolean enabled, String email) {
        Note note = ownedNote(id, email);
        switch (state) {
            case "pin" -> note.setPinned(enabled);
            case "archive" -> note.setArchived(enabled);
            case "trash" -> note.setTrashed(enabled);
            default -> throw new IllegalArgumentException("Unknown note state");
        }
        return NoteResponse.from(notes.save(note));
    }

    public void deletePermanently(Long id, String email) {
        Note note = ownedNote(id, email);
        if (!note.isTrashed()) throw new IllegalArgumentException("Move the note to trash first");
        notes.delete(note);
    }
}
