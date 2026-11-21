package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class NoteCsvImportService {
    private final NoteRepository notes;
    private final UserRepository users;

    public NoteCsvImportService(NoteRepository notes, UserRepository users) {
        this.notes = notes;
        this.users = users;
    }

    public void importFile(Path file, String email) throws IOException {
        AppUser owner = users.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Note> imported = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(file);
             CSVParser csv = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true)
                     .get().parse(reader)) {
            if (!csv.getHeaderNames().equals(List.of("title", "description"))) {
                throw new IllegalArgumentException("CSV header must be title,description");
            }
            for (CSVRecord row : csv) {
                if (row.size() != 2 || row.get("title").isBlank()) {
                    throw new IllegalArgumentException("Invalid CSV row " + row.getRecordNumber());
                }
                imported.add(new Note(row.get("title").trim(), row.get("description"), owner));
            }
        }
        notes.saveAll(imported);
    }
}
