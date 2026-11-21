package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.config.RabbitMqConfig;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import java.time.Instant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbit")
public class RabbitReminderPublisher {
    private final NoteRepository notes;
    private final RabbitTemplate rabbit;

    public RabbitReminderPublisher(NoteRepository notes, RabbitTemplate rabbit) {
        this.notes = notes;
        this.rabbit = rabbit;
    }

    @Scheduled(fixedDelay = 60000)
    public void publishDueReminders() {
        for (Note note : notes.findByReminderAtLessThanEqualAndReminderQueuedFalseAndReminderSentFalseAndTrashedFalse(Instant.now())) {
            rabbit.convertAndSend(RabbitMqConfig.REMINDER_QUEUE, note.getId().toString());
            note.setReminderQueued(true);
            notes.save(note);
        }
    }
}
