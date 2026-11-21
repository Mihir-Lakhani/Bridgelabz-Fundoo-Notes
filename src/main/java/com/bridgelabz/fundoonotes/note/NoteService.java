package com.bridgelabz.fundoonotes.note;

import com.bridgelabz.fundoonotes.user.AppUser;
import com.bridgelabz.fundoonotes.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

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
        note.setLabels(request.labels());
        note.setReminderAt(request.reminderAt());
        return NoteResponse.from(notes.save(note));
    }

    public List<NoteResponse> list(String email, String search, String label, Boolean archived) {
        return notes.findByOwnerIdAndTrashedFalse(owner(email).getId()).stream()
                .filter(note -> search == null || note.getTitle().toLowerCase().contains(search.toLowerCase())
                        || (note.getDescription() != null && note.getDescription().toLowerCase().contains(search.toLowerCase())))
                .filter(note -> label == null || note.getLabels().contains(label))
                .filter(note -> archived == null || note.isArchived() == archived)
                .map(NoteResponse::from).toList();
    }

    public NoteResponse get(Long id, String email) { return NoteResponse.from(ownedNote(id, email)); }

    public NoteResponse update(Long id, NoteRequest request, String email) {
        Note note = ownedNote(id, email);
        note.setTitle(request.title());
        note.setDescription(request.description());
        note.setLabels(request.labels());
        note.setReminderAt(request.reminderAt());
        return NoteResponse.from(notes.save(note));
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

    public void delete(Long id, String email) { setState(id, "trash", true, email); }
}
