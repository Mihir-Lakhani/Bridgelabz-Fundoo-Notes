package com.bridgelabz.fundoonotes.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notes")
public class NoteImportController {
    private final JobLauncher launcher;
    private final Job importJob;

    public NoteImportController(JobLauncher launcher, @Qualifier("noteImportJob") Job importJob) {
        this.launcher = launcher;
        this.importJob = importJob;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> importCsv(@RequestParam MultipartFile file,
                                         @AuthenticationPrincipal Jwt jwt) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("CSV file is empty");
        Path temporaryFile = Files.createTempFile("fundoo-import-", ".csv");
        try {
            file.transferTo(temporaryFile);
            var parameters = new JobParametersBuilder()
                    .addString("file", temporaryFile.toString())
                    .addString("email", jwt.getSubject())
                    .addString("request", UUID.randomUUID().toString())
                    .toJobParameters();
            var result = launcher.run(importJob, parameters);
            if (!result.getStatus().toString().equals("COMPLETED")) {
                throw new IllegalArgumentException("CSV import failed");
            }
            return Map.of("status", result.getStatus().toString());
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
