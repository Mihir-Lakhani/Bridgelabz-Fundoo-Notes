package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

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
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty() || !lines.getFirst().trim().equalsIgnoreCase("title,description")) {
            throw new IllegalArgumentException("CSV header must be title,description");
        }
        List<Note> imported = new ArrayList<>();
        for (int index = 1; index < lines.size(); index++) {
            if (lines.get(index).isBlank()) continue;
            String[] columns = lines.get(index).split(",", 2);
            if (columns.length != 2 || columns[0].isBlank()) {
                throw new IllegalArgumentException("Invalid CSV row " + (index + 1));
            }
            imported.add(new Note(columns[0].trim(), columns[1].trim(), owner));
        }
        notes.saveAll(imported);
    }
}
