package com.bridgelabz.fundoonotes.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteExportController {
    private final JobLauncher launcher;
    private final Job exportJob;

    public NoteExportController(JobLauncher launcher, @Qualifier("noteExportJob") Job exportJob) {
        this.launcher = launcher;
        this.exportJob = exportJob;
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@AuthenticationPrincipal Jwt jwt) throws Exception {
        Path temporaryFile = Files.createTempFile("fundoo-export-", ".csv");
        try {
            var parameters = new JobParametersBuilder()
                    .addString("file", temporaryFile.toString())
                    .addString("email", jwt.getSubject())
                    .addString("request", UUID.randomUUID().toString())
                    .toJobParameters();
            var result = launcher.run(exportJob, parameters);
            if (!result.getStatus().toString().equals("COMPLETED")) {
                throw new IllegalArgumentException("CSV export failed");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=notes.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(Files.readAllBytes(temporaryFile));
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
