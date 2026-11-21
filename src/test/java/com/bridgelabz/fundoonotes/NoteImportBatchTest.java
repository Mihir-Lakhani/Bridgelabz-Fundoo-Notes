package com.bridgelabz.fundoonotes;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.nio.file.Files;
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

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:import_test;DB_CLOSE_DELAY=-1")
class NoteImportBatchTest {
    @Autowired UserRepository users;
    @Autowired NoteRepository notes;
    @Autowired JobLauncher launcher;
    @Autowired @Qualifier("noteImportJob") Job importJob;
    @TempDir Path tempDir;

    @Test
    void batchJobImportsNotesForSpecifiedUser() throws Exception {
        AppUser user = users.save(new AppUser("Mihir", "import@example.com", "encoded-password"));
        Path file = tempDir.resolve("notes.csv");
        Files.writeString(file, "title,description\nFirst,One\nSecond,Two\n");
        var parameters = new JobParametersBuilder()
                .addString("file", file.toString())
                .addString("email", user.getEmail())
                .addString("request", UUID.randomUUID().toString())
                .toJobParameters();

        var result = launcher.run(importJob, parameters);

        assertEquals("COMPLETED", result.getStatus().toString());
        assertEquals(2, notes.findByOwnerId(user.getId()).size());
    }
}
