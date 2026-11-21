package com.bridgelabz.fundoonotes.note;

import jakarta.validation.constraints.NotBlank;

public record NoteRequest(@NotBlank String title, String description) { }
