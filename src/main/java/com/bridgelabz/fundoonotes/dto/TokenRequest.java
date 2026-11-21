package com.bridgelabz.fundoonotes.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String token) { }
