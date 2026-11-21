package com.bridgelabz.fundoonotes.dto;

import com.bridgelabz.fundoonotes.entity.Label;

public record LabelResponse(Long id, String name) {
    public static LabelResponse from(Label label) {
        return new LabelResponse(label.getId(), label.getName());
    }
}
