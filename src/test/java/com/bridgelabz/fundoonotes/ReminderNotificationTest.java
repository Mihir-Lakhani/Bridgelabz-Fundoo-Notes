package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.dto.NoteResponse;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import com.bridgelabz.fundoonotes.service.ReminderNotificationService;
import com.bridgelabz.fundoonotes.service.RabbitReminderConsumer;
import com.bridgelabz.fundoonotes.service.MailService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:notify_test;DB_CLOSE_DELAY=-1")
class ReminderNotificationTest {
    @Autowired UserRepository users;
    @Autowired NoteRepository notes;
    @Autowired ReminderNotificationService reminders;
    @Autowired MailService mail;

    @Test
    void dueReminderIsMarkedSent() {
        AppUser user = users.save(new AppUser("Mihir", "notify@example.com", "encoded-password"));
        Note note = new Note("Appointment", "Visit doctor", user);
        note.setReminderAt(Instant.now().minusSeconds(60));
        Long id = notes.save(note).getId();

        reminders.sendDueReminders();

        Note sent = notes.findById(id).orElseThrow();
        assertTrue(sent.isReminderSent());
        assertNull(sent.getReminderAt());
        reminders.sendDueReminders();
        assertNull(notes.findById(id).orElseThrow().getReminderAt());
    }

    @Test
    void rabbitConsumerHandlesNoteIdAndMarksReminderSent() {
        AppUser user = users.save(new AppUser("Mihir", "rabbit@example.com", "encoded-password"));
        Note note = new Note("Rabbit reminder", null, user);
        note.setReminderAt(Instant.now().minusSeconds(60));
        Long id = notes.save(note).getId();

        new RabbitReminderConsumer(notes, mail).sendReminder(id.toString());

        Note sent = notes.findById(id).orElseThrow();
        assertTrue(sent.isReminderSent());
        assertNull(sent.getReminderAt());
    }

    @Test
    void previouslySentReminderIsHiddenFromApiResponse() {
        AppUser user = users.save(new AppUser("Mihir", "old-reminder@example.com", "encoded-password"));
        Note oldNote = new Note("Old reminder", null, user);
        oldNote.setReminderAt(Instant.now().minusSeconds(60));
        oldNote.setReminderSent(true);

        assertNull(NoteResponse.from(oldNote).reminderAt());
    }
}
