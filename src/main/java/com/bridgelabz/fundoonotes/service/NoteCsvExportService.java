package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.entity.AppUser;
import com.bridgelabz.fundoonotes.entity.Note;
import com.bridgelabz.fundoonotes.repository.NoteRepository;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

@Service
public class NoteCsvExportService {
    private final NoteRepository notes;
    private final UserRepository users;

    public NoteCsvExportService(NoteRepository notes, UserRepository users) {
        this.notes = notes;
        this.users = users;
    }

    public void exportFile(Path file, String email) throws IOException {
        AppUser user = users.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        try (Writer writer = Files.newBufferedWriter(file);
             CSVPrinter csv = CSVFormat.DEFAULT.builder().setHeader("title", "description")
                     .get().print(writer)) {
            for (Note note : notes.findByOwnerId(user.getId())) {
                if (!note.isTrashed()) {
                    csv.printRecord(note.getTitle(), note.getDescription());
                }
            }
        }
    }
}
