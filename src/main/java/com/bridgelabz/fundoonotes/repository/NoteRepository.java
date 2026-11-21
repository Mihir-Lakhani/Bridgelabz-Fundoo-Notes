package com.bridgelabz.fundoonotes.repository;

import com.bridgelabz.fundoonotes.entity.Note;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerId(Long ownerId);
    Optional<Note> findByIdAndOwnerId(Long id, Long ownerId);
    List<Note> findByReminderAtLessThanEqualAndReminderSentFalseAndTrashedFalse(Instant now);
    List<Note> findByReminderAtLessThanEqualAndReminderQueuedFalseAndReminderSentFalseAndTrashedFalse(Instant now);
}
