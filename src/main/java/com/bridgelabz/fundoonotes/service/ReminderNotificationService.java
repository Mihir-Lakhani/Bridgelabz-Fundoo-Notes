package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReminderNotificationService {
    private final NoteRepository notes;
    private final MailService mail;

    public ReminderNotificationService(NoteRepository notes, MailService mail) {
        this.notes = notes;
        this.mail = mail;
    }

    @Scheduled(fixedDelay = 60000)
    public void sendDueReminders() {
        for (Note note : notes.findByReminderAtLessThanEqualAndReminderSentFalseAndTrashedFalse(Instant.now())) {
            mail.send(note.getOwner().getEmail(), "Note reminder", note.getTitle());
            note.setReminderSent(true);
            notes.save(note);
        }
    }
}
