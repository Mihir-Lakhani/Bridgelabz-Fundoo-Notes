package com.bridgelabz.fundoonotes.note;

public record NoteResponse(Long id, String title, String description,
                           boolean pinned, boolean archived, boolean trashed) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getDescription(),
                note.isPinned(), note.isArchived(), note.isTrashed());
    }
}
