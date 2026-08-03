package com.example;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = OwnerValidator.MAX_NAME_LENGTH)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    // NÃO cascade = CascadeType.REMOVE — FR-O5 quer bloquear, não cascatear.
    @OneToMany(mappedBy = "owner")
    private List<Pet> pets = new ArrayList<>();

    protected Owner() {
        // exigido pelo JPA
    }

    public Owner(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<Pet> getPets() { return pets; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}