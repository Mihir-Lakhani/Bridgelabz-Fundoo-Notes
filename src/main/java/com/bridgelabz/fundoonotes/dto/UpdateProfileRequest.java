package com.bridgelabz.fundoonotes.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(@NotBlank String name) { }
