package com.bridgelabz.fundoonotes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 5000)
    private String description;
    private boolean pinned;
    private boolean archived;
    private boolean trashed;
    private Instant trashedAt;
    @ManyToOne(optional = false)
    private AppUser owner;

    protected Note() { }

    public Note(String title, String description, AppUser owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isPinned() { return pinned; }
    public boolean isArchived() { return archived; }
    public boolean isTrashed() { return trashed; }
    public Instant getTrashedAt() { return trashedAt; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
        this.trashedAt = trashed ? Instant.now() : null;
    }
}
