package com.example;

/**
 * Validação de nome de pet.
 *
 * Nota real: um bug muito parecido com o que este validator previne foi
 * reportado recentemente no próprio Spring PetClinic (issue #2600 no GitHub:
 * "PetValidator lacks name length check") — o validador original aceitava
 * qualquer string não vazia, sem checar tamanho máximo. É exatamente o tipo
 * de lacuna que um teste de exemplo isolado ("nome 'Rex' é válido") não
 * pega, mas um teste de propriedade ("nenhum nome acima de 50 caracteres
 * deveria ser aceito") pega de cara — ver PetValidatorPropertyTest.
 */
public final class PetValidator {

    public static final int MAX_NAME_LENGTH = 50;

    private PetValidator() {
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String trimmed = name.strip();
        return !trimmed.isEmpty() && trimmed.length() <= MAX_NAME_LENGTH;
    }
}
