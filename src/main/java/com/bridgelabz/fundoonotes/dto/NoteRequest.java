package com.bridgelabz.fundoonotes.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;

public record NoteRequest(@NotBlank String title, String description,
                          Set<String> labels, LocalDateTime reminderAt) { }
