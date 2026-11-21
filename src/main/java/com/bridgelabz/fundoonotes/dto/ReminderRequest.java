package com.bridgelabz.fundoonotes.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReminderRequest(@NotNull @Future Instant reminderAt) { }
