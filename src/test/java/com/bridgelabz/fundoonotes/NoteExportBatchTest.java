package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:export_test;DB_CLOSE_DELAY=-1")
class NoteExportBatchTest {
    @Autowired UserRepository users;
    @Autowired NoteRepository notes;
    @Autowired JobLauncher launcher;
    @Autowired @Qualifier("noteImportJob") Job importJob;
    @Autowired @Qualifier("noteExportJob") Job exportJob;
    @TempDir Path tempDir;

    @Test
    void exportedCsvCanBeImportedWithoutLosingCommasOrQuotes() throws Exception {
        AppUser source = users.save(new AppUser("Source", "source@example.com", "encoded-password"));
        AppUser destination = users.save(new AppUser("Destination", "destination@example.com", "encoded-password"));
        notes.save(new Note("Plan, today", "Call \"Bob\"\nTomorrow", source));
        Note trash = new Note("Old", null, source);
        trash.setTrashed(true);
        notes.save(trash);
        Path file = tempDir.resolve("notes.csv");

        assertEquals("COMPLETED", launcher.run(exportJob, parameters(file, source.getEmail()))
                .getStatus().toString());
        assertEquals("COMPLETED", launcher.run(importJob, parameters(file, destination.getEmail()))
                .getStatus().toString());
        var copied = notes.findByOwnerId(destination.getId());
        assertEquals(1, copied.size());
        assertEquals("Plan, today", copied.getFirst().getTitle());
        assertEquals("Call \"Bob\"\nTomorrow", copied.getFirst().getDescription());
    }

    private org.springframework.batch.core.job.parameters.JobParameters parameters(Path file, String email) {
        return new JobParametersBuilder()
                .addString("file", file.toString())
                .addString("email", email)
                .addString("request", UUID.randomUUID().toString())
                .toJobParameters();
    }
}
