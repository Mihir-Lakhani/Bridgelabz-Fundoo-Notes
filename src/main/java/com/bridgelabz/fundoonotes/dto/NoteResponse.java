package com.bridgelabz.fundoonotes.dto;

import com.bridgelabz.fundoonotes.entity.Note;

public record NoteResponse(Long id, String title, String description,
                           boolean pinned, boolean archived, boolean trashed) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getDescription(),
                note.isPinned(), note.isArchived(), note.isTrashed());
    }
}
