package com.example;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public Pet registerPet(String name, LocalDate birthDate) {
        if (!PetValidator.isValidName(name)) {
            throw new IllegalArgumentException("Nome de pet inválido: '" + name + "'");
        }
        return repository.save(new Pet(name.trim(), birthDate));
    }
}
