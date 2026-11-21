package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import com.bridgelabz.fundoonotes.service.TrashCleanupService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:cleanup_test;DB_CLOSE_DELAY=-1")
class TrashCleanupTest {
    @Autowired UserRepository users;
    @Autowired NoteRepository notes;
    @Autowired TrashCleanupService cleanup;

    @Test
    void deletesOnlyTrashOlderThanRetentionPeriod() {
        AppUser user = users.save(new AppUser("Mihir", "cleanup@example.com", "encoded-password"));
        Note old = new Note("Old", null, user);
        old.setTrashed(true);
        old.setTrashedAt(Instant.now().minus(31, ChronoUnit.DAYS));
        Long oldId = notes.save(old).getId();
        Note recent = new Note("Recent", null, user);
        recent.setTrashed(true);
        Long recentId = notes.save(recent).getId();

        assertEquals(1, cleanup.deleteExpiredTrash());
        assertTrue(notes.findById(oldId).isEmpty());
        assertTrue(notes.findById(recentId).isPresent());
    }
}
