package com.example;

public class OwnerHasPetsException extends RuntimeException {
    public OwnerHasPetsException(Long ownerId) {
        super("Owner " + ownerId + " ainda tem pets vinculados e não pode ser removido");
    }
}