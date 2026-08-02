# clinic-pet-service — exemplo real de pipeline Java

Projeto pequeno, mas com a estrutura completa de um pipeline real: aplicação
Spring Boot + 3 tipos de teste (unitário, propriedade, integração) + CI no
GitHub Actions + Docker. O objetivo não é o domínio (cadastro de pet), e
sim mostrar exatamente o que você vai escrever e mexer como estagiário de
testing, com toda decisão de reprodutibilidade explicada.

## Onde está cada coisa

| Arquivo | O que é | Tipo de teste (relacionado) |
|---|---|---|
| `src/main/java/.../PetValidator.java` | lógica pura de validação | alvo do teste de propriedade |
| `src/main/java/.../PetService.java` | serviço com dependência (repository) | alvo do teste unitário |
| `src/test/java/.../PetServiceTest.java` | testa `PetService` com o repository **mockado** | unitário (Mockito) |
| `src/test/java/.../PetValidatorPropertyTest.java` | gera ~centenas de nomes automaticamente | propriedade (jqwik) |
| `src/test/java/.../PetRepositoryIT.java` | salva/busca num Postgres **real** (container) | integração (Testcontainers) |
| `.github/workflows/ci.yml` | roda tudo isso a cada push/PR | — |
| `.github/dependabot.yml` | propõe atualização de versão via PR, controlada | — |
| `docker-compose.yml` | sobe o mesmo Postgres localmente, pra dev manual | — |

## Como rodar (o que você vai digitar de verdade)

```bash
./mvnw test      # só unitário + propriedade — segundos, sem Docker
./mvnw verify     # unitário + propriedade + integração — precisa de Docker rodando
```

## As decisões de reprodutibilidade, uma por uma

Isto responde diretamente aos dois problemas clássicos que você mencionou.

### 1. "Sempre pegar `windows-latest` é foda"

O problema de fundo não é só "Windows vs. Linux" — é a palavra **`latest`**.
Labels como `ubuntu-latest` ou `windows-latest` apontam para uma imagem que
o GitHub troca por baixo dos seus pés: `ubuntu-latest` já foi remapeado de
22.04 para 24.04 (dezembro/2024–janeiro/2025) para todo mundo que usava o
label, no mesmo dia, sem ninguém ter mudado uma linha do próprio workflow.
Quem já estava pinado numa versão explícita não sentiu nada.

A correção, aplicada em `ci.yml`:
```yaml
runs-on: ubuntu-24.04   # não ubuntu-latest, e não windows-latest
```
Escolhemos Linux especificamente (não só "uma versão fixa de Windows")
porque a aplicação roda em container Linux em produção — testar em Linux
no CI também é sobre **paridade dev/prod**, não só sobre reprodutibilidade
por si só. Se um dia precisar validar algo Windows-específico de verdade,
isso vira um job **separado e deliberado**, não o padrão do projeto.

O mesmo raciocínio vale para as *actions* usadas (`actions/checkout@v7`,
`actions/setup-java@v6`): são versões majors fixas. Para o nível de rigor
máximo (comum em pipelines com preocupação forte de supply-chain), dá pra
ir além e fixar pelo **hash do commit** em vez do número de versão —
```yaml
uses: actions/checkout@<sha-completo-do-commit>
```
mas isso tem custo de manutenção (o Dependabot ajuda a automatizar até
isso, ele sabe atualizar SHAs também), então é uma escolha de quanto
rigor o time realmente precisa.

### 2. "Pegar dependência atualizada que quebra alguma coisa"

A causa raiz aqui quase nunca é "atualizar é ruim" — é atualizar **sem
controle**: version ranges (`[3.0,)`), tags Docker tipo `latest`, ou
simplesmente nunca revisar o changelog antes do bump. As duas pontas ruins
do espectro são "nunca atualizar" (acumula CVE sem correção) e "atualizar
sozinho, sempre" (quebra sem avisar).

O meio-termo, aplicado neste projeto:

- **Toda versão é exata**, em todo arquivo: `spring-boot-starter-parent`
  em `4.1.0` (não `4.+`), `postgres:17.2-alpine` (não `postgres:latest`
  nem só `postgres:17`), `testcontainers.version` em `1.21.2`. Nada de
  `RELEASE` ou `LATEST` — essas palavras-chave inclusive já foram banidas
  do Maven Central há anos, precisamente por quebrarem reprodutibilidade.
- **`.mvn/wrapper/maven-wrapper.properties`** fixa até a versão do
  **próprio Maven** (3.9.9) — sem isso, `mvn verify` roda com "qualquer
  Maven que estiver instalado na máquina", que também varia entre sua
  máquina, a do colega e o runner do CI. Por isso o workflow chama
  `./mvnw`, nunca `mvn` puro.
- **`.github/dependabot.yml`** é o que evita o outro extremo (nunca
  atualizar): toda semana, ele abre um PR *individual* propondo bump de
  uma dependência Maven, uma GitHub Action, ou a tag da imagem Docker —
  e esse PR roda o `ci.yml` normalmente, como qualquer outro. Você só
  aceita se passar E fizer sentido depois de olhar o changelog. É
  atualização deliberada, uma de cada vez, nunca silenciosa.
- Para o Postgres especificamente, dá pra ir a um nível ainda mais rígido
  que a tag — pinar pelo **digest sha256**, que é imutável de verdade
  (uma tag pode, em teoria, ser reapontada para outra imagem; um digest,
  não):
  ```java
  new PostgreSQLContainer<>(
      DockerImageName.parse("postgres@sha256:<digest-completo>")
          .asCompatibleSubstituteFor("postgres")
  )
  ```
  O trade-off: você perde a legibilidade humana ("ah, é a 17.2") e o bump
  automático do Dependabot para digest exige um pouco mais de configuração.
  Por isso a tag com patch fixo (`17.2-alpine`) costuma ser o equilíbrio
  padrão adotado — digest fica reservado para quando o time tem exigência
  de compliance/segurança que realmente peça essa garantia extra.

## Uma nota honesta

Este é um projeto mínimo para deixar o pipeline inteiro legível numa
sentada. Um projeto real de "sistema" vai ter mais camadas (controller,
DTOs, tratamento de erro HTTP, etc.) — mas a estrutura de testes e a
lógica de reprodutibilidade do pipeline são exatamente estas.
