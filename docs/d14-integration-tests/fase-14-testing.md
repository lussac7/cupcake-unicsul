# Fase 14 — Testing

**Projeto:** Cupcake-Unicsul  
**Artefato Maven:** `io.github.lussac7:cupcake`  
**Stack:** Java 21, Spring Boot 4.1.2, Maven, PostgreSQL, Spring Data JPA, Hibernate, Flyway, JUnit 5, Mockito e Testcontainers.  
**Status:** Concluída — `BUILD SUCCESS`

---

## 1. Objetivo

Consolidar e validar a suíte automatizada de testes do projeto, cobrindo regras de domínio, serviços, contratos HTTP, persistência e integração real com PostgreSQL.

A estratégia segue a arquitetura modular monolítica em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

## 2. Resultado da execução completa

Comando executado:

```bash
mvn clean test
```

Resultado:

| Indicador | Resultado |
|---|---:|
| Testes executados | 93 |
| Falhas | 0 |
| Erros | 0 |
| Ignorados | 0 |
| Build | **SUCCESS** |

A suíte completa foi executada em conjunto, incluindo os testes de integração que inicializam um PostgreSQL temporário por meio do Testcontainers.

## 3. Cobertura implementada

### 3.1 Testes unitários e de domínio

Foram revisados e ampliados os testes relacionados a:

- regras e invariantes de `Product`;
- regras e invariantes de `Order`;
- cálculo de subtotal e total;
- validação de dados e preservação do estado do produto quando uma atualização é inválida;
- comportamento de `ProductService` e `OrderService`;
- validação dos DTOs de entrada.

Entre os casos acrescentados ou reforçados estão:

- rejeição de preço nulo ou não positivo;
- rejeição de preço com mais de duas casas decimais;
- limites máximos de campos textuais;
- rejeição de identificador de produto não positivo;
- rejeição de quantidade negativa;
- normalização dos dados do cliente;
- paginação e ordenação dos resultados;
- tratamento de produtos inexistentes ou inativos.

### 3.2 Testes Web

Os testes dos controllers verificam:

- criação e consulta de recursos;
- validação das requisições;
- códigos HTTP;
- estrutura JSON das respostas;
- paginação e ordenação;
- rejeição de itens inválidos.

Os testes Web isolados usam `@WebMvcTest` e serviços simulados com `@MockitoBean`, de modo que validam o comportamento HTTP do controller sem depender do banco de dados.

### 3.3 Testes de persistência

Os testes de repositório usam `@DataJpaTest` e H2 em memória para verificar operações de persistência e consultas JPA.

O perfil de teste H2 está configurado em:

`src/test/resources/application-test.yml`

Configuração relevante:

```yaml
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

O H2 é usado nos testes de persistência isolados. Os comportamentos específicos do PostgreSQL são validados separadamente com Testcontainers.

## 4. Testes de integração com PostgreSQL

A classe de integração é:

`src/test/java/io/github/lussac7/cupcake/integration/PostgreSqlIntegrationTest.java`

O ambiente de integração usa um container PostgreSQL temporário, com a imagem `postgres:16-alpine`, iniciado pelo Testcontainers. A conexão é configurada com `@ServiceConnection`.

Os 14 testes de integração cobrem:

1. conexão com PostgreSQL;
2. aplicação da migração inicial do Flyway;
3. persistência e recuperação de produto com JPA;
4. rejeição de preço inválido pelo banco;
5. persistência de pedido com itens;
6. preservação do snapshot do item do pedido após alteração do produto;
7. integridade da chave estrangeira de produto em `order_items`;
8. constraint de subtotal de `order_items`;
9. integridade da chave estrangeira de pedido em `order_items`;
10. unicidade de produto por pedido;
11. criação de produto pela API REST;
12. recuperação de produto pela API REST;
13. criação de pedido pela API REST;
14. recuperação de pedido pela API REST.

Os testes REST de integração usam `MockMvc` com o contexto completo do Spring Boot. Diferentemente dos testes `@WebMvcTest`, esses fluxos percorrem controller, service, repository e PostgreSQL real.

## 5. Migração e constraints verificadas

A migração inicial `V1__initial_schema.sql` é executada pelo Flyway no PostgreSQL do Testcontainers.

Os testes verificam, entre outros aspectos:

- execução e registro da migração V1;
- restrição de preço positivo;
- integridade referencial entre pedidos, itens e produtos;
- unicidade de produto dentro de um pedido;
- consistência do subtotal em relação ao preço unitário e à quantidade.

## 6. Dependências de teste relevantes

Foram utilizadas as dependências de teste do Spring Boot e:

- `spring-boot-testcontainers`;
- `testcontainers-postgresql`;
- `testcontainers-junit-jupiter`;
- H2 para testes JPA isolados.

As versões são gerenciadas pela configuração de dependências do projeto e pelo parent Spring Boot selecionado.

## 7. Comandos de execução

Executar somente os testes de integração PostgreSQL:

```bash
mvn -Dtest=PostgreSqlIntegrationTest test
```

Executar testes específicos, por exemplo:

```bash
mvn -Dtest=ProductServiceTest,OrderServiceTest test
```

Executar a suíte completa, limpando os artefatos anteriores:

```bash
mvn clean test
```

O último comando foi executado com sucesso: **93 testes, zero falhas, zero erros e zero testes ignorados**.

## 8. Decisões e observações

- Testcontainers foi usado para validar comportamento dependente do PostgreSQL, evitando tratar H2 como substituto integral do banco de produção.
- Não foi necessário criar Docker Compose para os testes de integração; o próprio Testcontainers administra o container temporário.
- Os testes de constraints que provocam falhas SQL não devem ser envolvidos inadvertidamente em uma transação de teste que impeça a continuidade após a falha, devido ao estado abortado da transação PostgreSQL.
- Os testes de controller isolados e os testes REST com contexto completo têm objetivos diferentes e complementares.
- A suíte foi executada integralmente depois dos incrementos da fase, confirmando compatibilidade conjunta entre os testes.

## 9. Critério de conclusão

A fase 14 está concluída porque:

- existem testes de domínio e serviços;
- os controllers têm testes Web;
- os repositórios têm testes de persistência;
- há integração com PostgreSQL real via Testcontainers;
- os fluxos REST de produto e pedido foram exercitados com banco real;
- a suíte completa executou sem falhas.

**Resultado final: Fase 14 — Testing concluída.**

---

## 10. Próxima fase

A próxima etapa é a **Fase 15 — Test Strategy**.

O trabalho será uma avaliação estratégica da cobertura por funcionalidade, identificando:

- happy path;
- entradas inválidas;
- casos-limite;
- violações de regras de negócio;
- falhas de persistência;
- concorrência, quando relevante.

A fase 15 deve priorizar a utilidade dos testes e a redução de riscos, não o aumento indiscriminado da quantidade de testes.
