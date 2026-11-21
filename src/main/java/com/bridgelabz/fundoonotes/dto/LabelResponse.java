package com.bridgelabz.fundoonotes.dto;

import com.bridgelabz.fundoonotes.entity.Label;
import java.io.Serializable;

public record LabelResponse(Long id, String name) implements Serializable {
    public static LabelResponse from(Label label) {
        return new LabelResponse(label.getId(), label.getName());
    }
}
