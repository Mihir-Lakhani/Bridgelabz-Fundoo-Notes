package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.LabelResponse;
import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Label;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.LabelRepository;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class LabelService {
    private final LabelRepository labels;
    private final NoteRepository notes;
    private final UserRepository users;

    public LabelService(LabelRepository labels, NoteRepository notes, UserRepository users) {
        this.labels = labels;
        this.notes = notes;
        this.users = users;
    }

    private AppUser owner(String email) {
        return users.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public LabelResponse create(String name, String email) {
        AppUser user = owner(email);
        String cleanName = name.trim();
        if (labels.existsByOwnerIdAndNameIgnoreCase(user.getId(), cleanName)) {
            throw new IllegalArgumentException("Label already exists");
        }
        return LabelResponse.from(labels.save(new Label(cleanName, user)));
    }

    public List<LabelResponse> list(String email) {
        return labels.findByOwnerId(owner(email).getId()).stream().map(LabelResponse::from).toList();
    }

    public void delete(Long id, String email) {
        Long ownerId = owner(email).getId();
        Label label = labels.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NoSuchElementException("Label not found"));
        for (Note note : notes.findByOwnerId(ownerId)) {
            if (note.getLabels().removeIf(existing -> existing.getId().equals(id))) {
                notes.save(note);
            }
        }
        labels.delete(label);
    }
}
