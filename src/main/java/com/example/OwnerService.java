package com.example;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private final OwnerRepository repository;

    public OwnerService(OwnerRepository repository) {
        this.repository = repository;
    }

    public Owner registerOwner(String name, String phone) {
        if (!OwnerValidator.isValidName(name)) {
            throw new IllegalArgumentException("Nome de owner inválido: '" + name + "'");
        }
        if (!OwnerValidator.isValidPhone(phone)) {
            throw new IllegalArgumentException("Telefone de owner inválido: '" + phone + "'");
        }
        try {
            return repository.save(new Owner(name.strip(), phone));
        } catch (DataIntegrityViolationException e) {
            // unicidade (FR-O2) é garantida pela constraint UNIQUE do banco
            // (evita race condition entre um "existsByPhone" e o save);
            // aqui só traduzimos o erro genérico de banco pra um erro de
            // domínio com significado, em vez de deixar vazar pra fora.
            throw new DuplicatePhoneException(phone);
        }
    }

    public void deleteOwner(Long ownerId) {
        Owner owner = repository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner não encontrado: " + ownerId));
        if (!owner.getPets().isEmpty()) {
            throw new OwnerHasPetsException(ownerId);
        }
        repository.delete(owner);
    }
}