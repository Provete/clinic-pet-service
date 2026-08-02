package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Teste de INTEGRAÇÃO: sobe um Postgres real (dentro de um container Docker
 * de verdade) só para a duração deste teste. É aqui que mapeamento JPA,
 * tipos de coluna e SQL gerado de fato são exercitados — o que o
 * PetServiceTest, com repository mockado, nunca poderia pegar.
 *
 * O nome termina em "IT" (não "Test") de propósito: é a convenção padrão do
 * Maven Failsafe (Parte 3 do texto de ferramentas). Isso faz esta classe
 * NÃO rodar em "mvn test" (rápido, sem Docker) e SÓ rodar em "mvn verify"
 * (mais lento, precisa de Docker disponível) — é assim que o pipeline
 * separa as duas velocidades sem nenhuma configuração extra no pom.xml.
 *
 * A anotação @ServiceConnection (Spring Boot 3.1+) é o que conecta esse container
 * automaticamente à aplicação Spring — sem ele, seria preciso configurar
 * manualmente a URL/usuário/senha via @DynamicPropertySource.
 */
@Testcontainers
@SpringBootTest
class PetRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.2-alpine");
    // Tag fixa com patch (17.2), não "postgres:latest" nem só "postgres:17".
    // Ver README.md para a opção ainda mais rígida (pin por digest sha256).

    @Autowired
    private PetRepository repository;

    @Test
    void devePersistirEBuscarPetNoBancoReal() {
        Pet salvo = repository.save(new Pet("Bidu", LocalDate.of(2019, 5, 20)));

        Optional<Pet> encontrado = repository.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getName()).isEqualTo("Bidu");
    }
}
