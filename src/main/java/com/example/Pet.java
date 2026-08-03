package com.example;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDate birthDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable = false)
    private Owner owner;

    protected Pet() {
        // exigido pelo JPA
    }

    public Pet(String name, LocalDate birth_date) {
        this.name = name;
        this.birthDate = birth_date;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
