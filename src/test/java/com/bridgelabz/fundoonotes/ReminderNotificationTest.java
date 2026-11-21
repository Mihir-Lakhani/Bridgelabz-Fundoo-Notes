package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import com.bridgelabz.fundoonotes.service.ReminderNotificationService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:notify_test;DB_CLOSE_DELAY=-1")
class ReminderNotificationTest {
    @Autowired UserRepository users;
    @Autowired NoteRepository notes;
    @Autowired ReminderNotificationService reminders;

    @Test
    void dueReminderIsMarkedSent() {
        AppUser user = users.save(new AppUser("Mihir", "notify@example.com", "encoded-password"));
        Note note = new Note("Appointment", "Visit doctor", user);
        note.setReminderAt(Instant.now().minusSeconds(60));
        Long id = notes.save(note).getId();

        reminders.sendDueReminders();

        assertTrue(notes.findById(id).orElseThrow().isReminderSent());
    }
}
