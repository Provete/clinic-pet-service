package com.example;

public class DuplicatePhoneException extends RuntimeException {
    public DuplicatePhoneException(String phone) {
        super("Já existe um owner cadastrado com o telefone " + phone);
    }
}