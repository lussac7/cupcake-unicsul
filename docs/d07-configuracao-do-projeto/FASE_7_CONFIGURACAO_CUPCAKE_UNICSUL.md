# Fase 7 --- Configuração do Projeto

**Projeto:** Cupcake Unicsul\
**Identificador:** `io.github.lussac7:cupcake`\
**Status:** Concluída\
**Validação:** `mvn clean test` --- `BUILD SUCCESS`, confirmado pelo
usuário.

## 1. Objetivo

Configurar as dependências e os ambientes da aplicação Spring Boot,
incluindo persistência com PostgreSQL, JPA/Hibernate, Flyway, perfis de
execução, variáveis de ambiente e logging.

## 2. Dependências Maven

O `pom.xml` foi configurado com:

-   `spring-boot-starter-webmvc`: API REST e servidor web embutido.
-   `spring-boot-starter-data-jpa`: persistência com Spring Data JPA e
    Hibernate.
-   `postgresql`: driver JDBC do PostgreSQL, com escopo `runtime`.
-   `spring-boot-starter-flyway`: integração do Spring Boot com Flyway.
-   `flyway-database-postgresql`: suporte do Flyway ao PostgreSQL.
-   `spring-boot-starter-test`: infraestrutura de testes, com escopo
    `test`.
-   `h2`: banco em memória para testes isolados, com escopo `test`.

O projeto utiliza Spring Boot `4.1.2` e Java `21`.

## 3. Configuração geral

Arquivo: `src/main/resources/application.yml`

``` yaml
spring:
  application:
    name: cupcake

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true

logging:
  level:
    root: INFO
    io.github.lussac7.cupcake: INFO
```

Decisões principais:

-   `open-in-view: false`: desativa Open EntityManager in View.
-   `ddl-auto: validate`: Hibernate valida o esquema existente; não cria
    nem atualiza tabelas.
-   Flyway habilitado para controlar as migrações do banco.
-   Logging global e do pacote da aplicação em nível `INFO`.

## 4. Perfil de desenvolvimento

Arquivo: `src/main/resources/application-dev.yml`

``` yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/cupcake}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

logging:
  level:
    io.github.lussac7.cupcake: DEBUG
```

O perfil `dev` utiliza PostgreSQL. A URL possui um valor padrão para
desenvolvimento local. Usuário e senha são obtidos de variáveis de
ambiente, sem valores padrão.

Variáveis utilizadas:

-   `DB_URL`
-   `DB_USERNAME`
-   `DB_PASSWORD`
-   `SPRING_PROFILES_ACTIVE`

Exemplo de configuração no terminal Linux:

``` bash
export DB_URL="jdbc:postgresql://localhost:5432/cupcake"
export DB_USERNAME="seu_usuario_postgres"
export DB_PASSWORD="sua_senha_postgres"
export SPRING_PROFILES_ACTIVE=dev
```

Para ativar o perfil somente durante a execução do Maven:

``` bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

No perfil de desenvolvimento, o logging do pacote da aplicação é elevado
para `DEBUG`; as demais bibliotecas não são colocadas globalmente nesse
nível.

## 5. Perfil de testes

Arquivo: `src/main/resources/application-test.yml`

``` yaml
spring:
  datasource:
    url: jdbc:h2:mem:cupcake_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop

  flyway:
    enabled: false
```

Decisões principais:

-   H2 em memória para testes isolados.
-   `MODE=PostgreSQL` aproxima alguns comportamentos do PostgreSQL, mas
    não garante compatibilidade total.
-   `DB_CLOSE_DELAY=-1` mantém o banco durante a execução da JVM.
-   `ddl-auto: create-drop` permite ao Hibernate criar e remover as
    tabelas no ciclo de testes.
-   Flyway desabilitado nesse perfil.
-   Testes que iniciam o contexto Spring podem ativar o perfil com
    `@ActiveProfiles("test")`.

H2 é utilizado para testes isolados. Não substitui testes de integração
com PostgreSQL real, previstos para uma etapa posterior com
Testcontainers.

## 6. Estratégia de segurança das configurações

-   Não armazenar senhas reais ou segredos no repositório Git.
-   Não definir valores padrão para `DB_USERNAME` e `DB_PASSWORD`.
-   Fornecer credenciais por variáveis de ambiente.
-   Em produção, utilizar o ambiente de execução ou um gerenciador de
    segredos.
-   O arquivo `.gitignore` deve excluir arquivos locais de ambiente,
    como `.env`.

## 7. Verificação integrada

O usuário executou, na raiz do projeto:

``` bash
mvn clean test
```

Resultado informado:

``` text
BUILD SUCCESS
```

A verificação integrada da Fase 7 foi, portanto, concluída com sucesso.

## 8. Observação para a próxima fase

A aplicação ainda não deve ser considerada pronta para iniciar contra um
banco PostgreSQL vazio. O Hibernate está configurado com
`ddl-auto: validate` e as migrações Flyway ainda serão criadas.

A Fase 8 deverá implementar o esquema definido na Fase 5 por meio da
migração inicial `V1__initial_schema.sql`. Após a criação da migração,
será possível validar a inicialização da aplicação com PostgreSQL.

## 9. Resultado da fase

Concluídos:

-   Dependências Maven de web, JPA, PostgreSQL, Flyway e testes.
-   Configuração geral da aplicação.
-   Perfil de desenvolvimento com PostgreSQL.
-   Perfil de testes com H2.
-   Estratégia de variáveis de ambiente.
-   Configuração de logging.
-   Verificação integrada com `mvn clean test` e resultado
    `BUILD SUCCESS`.

**Fase 7 --- Configuração do Projeto: concluída.**
