package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.config.RabbitMqConfig;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import java.time.Instant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit")
public class RabbitReminderConsumer {
    private final NoteRepository notes;
    private final MailService mail;

    public RabbitReminderConsumer(NoteRepository notes, MailService mail) {
        this.notes = notes;
        this.mail = mail;
    }

    @RabbitListener(queues = RabbitMqConfig.REMINDER_QUEUE)
    public void sendReminder(String noteId) {
        Note note = notes.findById(Long.valueOf(noteId)).orElse(null);
        if (note == null || note.isTrashed() || note.isReminderSent()
                || note.getReminderAt() == null || note.getReminderAt().isAfter(Instant.now())) {
            return;
        }
        mail.send(note.getOwner().getEmail(), "Note reminder", note.getTitle());
        note.markReminderSent();
        notes.save(note);
    }
}
