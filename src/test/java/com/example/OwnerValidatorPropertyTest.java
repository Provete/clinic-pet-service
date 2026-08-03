package com.example;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

public class OwnerValidatorPropertyTest {

    private static final int MAX_STRING_LENGTH_TEST = 300;
    @Property
    boolean nomesAcimaDoLimiteSaoSempreRejeitados(
            @ForAll @AlphaChars @StringLength(min = OwnerValidator.MAX_NAME_LENGTH + 1, max = MAX_STRING_LENGTH_TEST) String nomeLongo
    )
    { return !OwnerValidator.isValidName(nomeLongo); }

    @Property
    boolean numerosDeTelefoneSaoTodosDigitos(
            @ForAll @StringLength(min=OwnerValidator.MIN_PHONE_LENGTH, max=OwnerValidator.MAX_NAME_LENGTH) String phone
    )
    { return !OwnerValidator.isValidPhone(phone); }

    @Property
    boolean numerosDeTelefoneForaDaFaixaSaoSempreRejeitados(
            @ForAll @AlphaChars @StringLength(min=0, max=MAX_STRING_LENGTH_TEST) String phone
    )
    { return !OwnerValidator.isValidPhone(phone); }
}
