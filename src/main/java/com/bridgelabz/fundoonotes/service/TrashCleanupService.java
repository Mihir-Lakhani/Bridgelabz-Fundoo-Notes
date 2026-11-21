package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TrashCleanupService {
    private final NoteRepository notes;
    private final long retentionDays;

    public TrashCleanupService(NoteRepository notes,
                               @Value("${fundoo.trash.retention-days:30}") long retentionDays) {
        this.notes = notes;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public int deleteExpiredTrash() {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        List<Note> expired = notes.findByTrashedTrueAndTrashedAtBefore(cutoff);
        notes.deleteAll(expired);
        return expired.size();
    }
}
