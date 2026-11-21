package com.bridgelabz.fundoonotes.dto;

import jakarta.validation.constraints.NotBlank;

public record LabelRequest(@NotBlank String name) { }
