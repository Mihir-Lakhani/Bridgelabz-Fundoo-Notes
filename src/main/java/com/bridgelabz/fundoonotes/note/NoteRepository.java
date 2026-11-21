package com.bridgelabz.fundoonotes.note;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerIdAndTrashedFalse(Long ownerId);
    Optional<Note> findByIdAndOwnerId(Long id, Long ownerId);
}
