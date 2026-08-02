package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Teste UNITÁRIO: o repository é mockado — não existe banco de dados real
 * aqui. É rápido (milissegundos) e roda a cada "mvn test", inclusive local
 * antes de qualquer commit. O que ele garante: a lógica de PetService está
 * certa, dado um repository que se comporta como esperado.
 * O que ele NÃO garante: que o repository de verdade (mapeamento JPA,
 * SQL gerado, etc.) funciona — isso é papel do PetRepositoryIT.
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository repository;

    @InjectMocks
    private PetService service;

    @Test
    void deveRejeitarNomeEmBrancoSemTocarNoRepository() {
        assertThatThrownBy(() -> service.registerPet("   ", LocalDate.of(2020, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);

        // ponto importante de um teste "mockista" bem feito: além do
        // resultado, verificamos que o repository nem foi chamado —
        // é exatamente esse tipo de asserção que fica frágil se usada
        // em excesso (Parte 6.1 do relatório), então reservamos verify()
        // só para o que realmente importa aqui: "não deveria ter salvo nada".
        verifyNoInteractions(repository);
    }

    @Test
    void deveSalvarComNomeValidoETrimado() {
        when(repository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pet resultado = service.registerPet("  Rex  ", LocalDate.of(2020, 1, 1));

        assertThat(resultado.getName()).isEqualTo("Rex");
        verify(repository).save(any(Pet.class));
    }
}
