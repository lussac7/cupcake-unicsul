# Fase 10 — DTOs e Validação
## Projeto Cupcake Unicsul

**Status:** Concluída  
**Stack:** Java 21, Spring Boot 4.1.2, Jakarta Bean Validation

## 1. Objetivo

Definir os objetos de transferência de dados (DTOs) utilizados nas fronteiras da API e estabelecer validações sintáticas para os dados recebidos. As entidades JPA não devem ser expostas diretamente pela API REST.

## 2. Dependência adicionada

No `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 3. DTOs implementados

### Módulo Product

| Arquivo | Finalidade |
|---|---|
| `ProductCreateRequest.java` | Dados necessários para cadastrar um produto |
| `ProductUpdateRequest.java` | Dados necessários para atualizar um produto |
| `ProductResponse.java` | Dados de produto retornados pela API |

Os DTOs de criação e atualização validam nome, descrição, preço e URL opcional da imagem. O preço utiliza `BigDecimal`, com validação de valor positivo e precisão decimal.

### Módulo Order

| Arquivo | Finalidade |
|---|---|
| `OrderItemRequest.java` | Identificador do produto e quantidade solicitada |
| `OrderCreateRequest.java` | Dados do cliente, endereço de entrega e itens do pedido |
| `OrderItemResponse.java` | Dados de cada item retornados na resposta |
| `OrderResponse.java` | Dados gerais do pedido e sua lista de itens |

O cliente não envia preço, subtotal ou total. Esses valores serão obtidos e calculados pelo backend. A resposta preserva os snapshots históricos do nome e do preço unitário dos produtos.

`OrderCreateRequest` utiliza `@Valid` nos elementos da lista para validar também cada `OrderItemRequest`.

## 4. Validação e responsabilidades

A separação definida para o projeto é:

- **DTOs e Jakarta Bean Validation:** validação sintática dos dados recebidos, como campos obrigatórios, tamanhos, formato de e-mail e valores positivos.
- **Domínio:** proteção das invariantes das entidades, como quantidade positiva, preço válido e pedido com pelo menos um item.
- **Serviços (Fase 11):** validação das regras de negócio, como existência e disponibilidade dos produtos durante o checkout.

A validação automática na camada REST será acionada pelo uso de `@Valid` nos parâmetros dos controllers.

## 5. Testes

Arquivo criado:

`src/test/java/io/github/lussac7/cupcake/dto/DTOValidationTest.java`

Os testes verificam:

- criação de produto com dados válidos;
- rejeição de dados inválidos em criação de produto;
- atualização de produto com dados válidos;
- rejeição de dados inválidos em atualização;
- criação de pedido válido;
- rejeição de pedido sem itens;
- validação aninhada de itens do pedido;
- rejeição de e-mail inválido.

## 6. Estrutura relevante

```text
src/
├── main/
│   └── java/io/github/lussac7/cupcake/
│       ├── product/dto/
│       │   ├── ProductCreateRequest.java
│       │   ├── ProductUpdateRequest.java
│       │   └── ProductResponse.java
│       └── order/dto/
│           ├── OrderItemRequest.java
│           ├── OrderCreateRequest.java
│           ├── OrderItemResponse.java
│           └── OrderResponse.java
└── test/
    ├── java/io/github/lussac7/cupcake/
    │   ├── dto/
    │   │   └── DTOValidationTest.java
    │   ├── product/
    │   │   ├── entity/ProductTest.java
    │   │   └── repository/ProductRepositoryTest.java
    │   └── order/
    │       ├── entity/OrderTest.java
    │       └── repository/OrderRepositoryTest.java
    └── resources/
        └── application-test.yml
```

## 7. Verificação

O usuário confirmou a execução bem-sucedida de:

```bash
mvn clean test
```

Resultado informado: `BUILD SUCCESS`.

## 8. Resultado da fase

A Fase 10 está concluída. Os DTOs de entrada e saída foram definidos e os principais cenários de validação foram testados.

A próxima etapa prevista é a **Fase 11 — Serviços e Regras de Negócio**, respeitando o desenvolvimento incremental definido no prompt mestre.
