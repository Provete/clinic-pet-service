package com.example;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.constraints.Whitespace;

/**
 * Teste de PROPRIEDADE: em vez de escolher exemplos ("Rex" é válido, ""
 * não é), declaramos invariantes e o jqwik gera de centenas a milhares de
 * strings aleatórias tentando quebrá-las. Quando acha uma entrada que
 * falha, reduz ("shrink") até o menor caso que ainda quebra — por isso o
 * relatório final de falha do jqwik costuma já vir com o contra exemplo
 * mínimo, pronto para virar um teste de exemplo permanente se você quiser.
 * Esta classe é o teste que teria pegado, sem ninguém precisar pensar nele
 * manualmente, o bug real citado em PetValidator (nome sem limite de
 * tamanho) — a propriedade abaixo simplesmente afirma o invariante e deixa
 * a ferramenta procurar contra-exemplos.
 */
class PetValidatorPropertyTest {

    @Property
    boolean nomesAcimaDoLimiteSaoSempreRejeitados(
            @ForAll @AlphaChars @StringLength(min = PetValidator.MAX_NAME_LENGTH + 1, max = 300) String nomeLongo) {
        return !PetValidator.isValidName(nomeLongo);
    }

    @Property
    boolean nomesSoDeEspacoSaoSempreRejeitados(
            @ForAll @Whitespace @StringLength(min = 1, max = 20) String nomeEmBranco) {
        return !PetValidator.isValidName(nomeEmBranco);
    }

    @Property
    boolean nomesAlfabeticosDentroDoLimiteSaoSempreAceitos(
            @ForAll @AlphaChars @StringLength(min = 1, max = PetValidator.MAX_NAME_LENGTH) String nomeValido) {
        return PetValidator.isValidName(nomeValido);
    }
}
