package com.bridgelabz.fundoonotes.dto;

import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.entity.Label;
import java.util.Set;
import java.time.Instant;
import java.util.stream.Collectors;

public record NoteResponse(Long id, String title, String description,
                           boolean pinned, boolean archived, boolean trashed,
                           Set<String> labels, Instant reminderAt) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getDescription(),
                note.isPinned(), note.isArchived(), note.isTrashed(),
                note.getLabels().stream().map(Label::getName).collect(Collectors.toSet()),
                note.isReminderSent() ? null : note.getReminderAt());
    }
}
