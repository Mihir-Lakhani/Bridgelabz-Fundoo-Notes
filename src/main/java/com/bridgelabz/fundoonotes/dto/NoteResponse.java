package com.bridgelabz.fundoonotes.dto;

import com.bridgelabz.fundoonotes.entity.Note;

import java.time.LocalDateTime;
import java.util.Set;

public record NoteResponse(Long id, String title, String description,
                           boolean pinned, boolean archived, boolean trashed,
                           Set<String> labels, LocalDateTime reminderAt) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getDescription(),
                note.isPinned(), note.isArchived(), note.isTrashed(),
                Set.copyOf(note.getLabels()), note.getReminderAt());
    }
}
