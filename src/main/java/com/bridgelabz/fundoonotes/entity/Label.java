package com.bridgelabz.fundoonotes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Label {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(optional = false)
    private AppUser owner;

    protected Label() { }

    public Label(String name, AppUser owner) {
        this.name = name;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
