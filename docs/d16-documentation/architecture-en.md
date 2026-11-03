# Arquitetura do Sistema — Cupcake Unicsul

## 1. Visão geral

O Cupcake Unicsul é uma aplicação acadêmica para gerenciamento de produtos e pedidos de uma loja de cupcakes.

O backend utiliza uma arquitetura de monólito modular (*Modular Monolith*) organizada em camadas (*Layered Architecture*).

A aplicação é desenvolvida com Java 21 e Spring Boot 4.1.2, utilizando PostgreSQL como banco de dados principal.

## 2. Estilo arquitetural

A aplicação organiza suas responsabilidades em módulos de domínio e camadas técnicas.

Fluxo principal:

```text
REST API
   |
   v
Controller Layer
   |
   v
Service Layer
   |
   v
Repository Layer
   |
   v
JPA / Hibernate
   |
   v
PostgreSQL
```

Os controllers recebem requisições HTTP, validam os DTOs e delegam as operações aos serviços.

Os serviços implementam as regras de negócio e utilizam os repositórios para persistência.

As entidades representam os objetos de domínio persistidos pelo JPA.

## 3. Organização dos módulos

O pacote principal é:

```text
io.github.lussac7.cupcake
```

Os módulos identificados são:

### 3.1 Módulo Product

Pacote:

```text
product/
├── controller/
├── dto/
├── entity/
├── repository/
└── service/
```

Responsabilidades:

- Gerenciar produtos.
- Cadastrar produtos.
- Atualizar informações dos produtos.
- Ativar e desativar produtos.
- Consultar produtos ativos.
- Disponibilizar consultas paginadas.

A entidade `Product` representa um produto e mantém seu estado de ativação.

A desativação é lógica: o registro permanece no banco de dados.

### 3.2 Módulo Order

Pacote:

```text
order/
├── controller/
├── dto/
├── entity/
├── repository/
└── service/
```

Responsabilidades:

- Criar pedidos.
- Registrar os dados do cliente.
- Associar produtos aos pedidos.
- Calcular subtotais e total.
- Consultar pedidos por identificador.
- Consultar pedidos de forma paginada.

A entidade `Order` representa o pedido e mantém uma coleção de `OrderItem`.

Cada item registra o nome e o preço unitário do produto no momento da compra, preservando essas informações históricas.

### 3.3 Módulo Shared

Pacote:

```text
shared/
└── exception/
```

Responsabilidades:

- Centralizar exceções utilizadas pelos módulos.
- Tratar erros globalmente.
- Padronizar respostas de erro da API.

Principais classes:

- `BusinessRuleException`
- `ResourceNotFoundException`
- `GlobalExceptionHandler`

## 4. Camadas da aplicação

### 4.1 Controller Layer

Responsável pela interface HTTP.

Controllers identificados:

- `ProductController`
- `OrderController`

Responsabilidades:

- Mapear endpoints REST.
- Receber requisições.
- Aplicar validação dos DTOs com Jakarta Validation.
- Retornar respostas HTTP.
- Delegar operações aos serviços.

As entidades JPA não são expostas diretamente pela API.

### 4.2 Service Layer

Responsável pelas operações de aplicação e regras de negócio.

Serviços identificados:

- `ProductService`
- `OrderService`

Os serviços utilizam injeção de dependências por construtor.

As operações de consulta utilizam transações somente de leitura por padrão. Operações de escrita possuem `@Transactional`.

### 4.3 Repository Layer

Responsável pela persistência.

Repositórios identificados:

- `ProductRepository`
- `OrderRepository`

Ambos utilizam Spring Data JPA.

O `ProductRepository` disponibiliza consultas específicas para produtos ativos.

### 4.4 Domain Layer

Representada pelas entidades:

- `Product`
- `Order`
- `OrderItem`

As entidades encapsulam regras e invariantes relacionadas aos seus respectivos objetos.

Exemplos:

- O preço de um produto deve ser positivo.
- Um produto inativo não pode ser incluído em um pedido.
- A quantidade de um item deve ser positiva.
- Um pedido deve conter pelo menos um item.
- O total do pedido é calculado a partir dos subtotais.

## 5. Transferência de dados

A API utiliza DTOs para separar o modelo de domínio do contrato HTTP.

DTOs de produto:

- `ProductCreateRequest`
- `ProductUpdateRequest`
- `ProductResponse`

DTOs de pedido:

- `OrderCreateRequest`
- `OrderItemRequest`
- `OrderResponse`
- `OrderItemResponse`

As validações de entrada utilizam anotações do Jakarta Validation.

## 6. Tratamento de exceções

A classe `GlobalExceptionHandler`, anotada com `@RestControllerAdvice`, centraliza o tratamento de exceções.

As respostas de erro utilizam `ProblemDetail`.

Principais mapeamentos:

| Exceção | HTTP |
|---|---:|
| ResourceNotFoundException | 404 |
| BusinessRuleException | 409 |
| MethodArgumentNotValidException | 400 |
| HttpMessageNotReadableException | 400 |
| Outras exceções não tratadas | 500 |

Erros inesperados são registrados por meio do SLF4J.

## 7. Persistência e migrações

A aplicação utiliza:

- PostgreSQL no perfil de desenvolvimento.
- H2 em memória no perfil de testes.
- Spring Data JPA.
- Hibernate.
- Flyway.

A migração inicial está localizada em:

```text
src/main/resources/db/migration/
└── V1__initial_schema.sql
```

No perfil principal, o Hibernate utiliza `ddl-auto: validate`.

No perfil de testes, utiliza `ddl-auto: create-drop`, com Flyway desabilitado.

## 8. Configuração

Arquivos identificados:

```text
src/main/resources/
├── application.yml
├── application-dev.yml
└── db/
    └── migration/
        └── V1__initial_schema.sql

src/test/resources/
└── application-test.yml
```

As configurações específicas de desenvolvimento utilizam variáveis de ambiente para as credenciais do PostgreSQL.

## 9. Testes

A suíte de testes está organizada por módulo e responsabilidade.

Inclui:

- Testes de entidades.
- Testes de serviços.
- Testes de controllers.
- Testes de repositórios.
- Testes de validação de DTOs.
- Testes do tratamento global de exceções.
- Testes de integração com PostgreSQL utilizando Testcontainers.

O perfil de testes utiliza H2 em memória para os testes que não necessitam de PostgreSQL real.

## 10. Estado do frontend

Na versão documentada, não foram identificados arquivos de frontend HTML, CSS ou JavaScript.

A interface web será desenvolvida em uma fase posterior, utilizando HTML5, CSS3 e JavaScript Vanilla, conforme planejamento do projeto.

Essa futura implementação deverá consumir a API REST existente.

## 11. Limitações atuais

A implementação atual não possui:

- Autenticação de usuários.
- Autorização de operações administrativas.
- Integração com serviço de pagamento.
- Upload de imagens.
- Interface frontend implementada.

Essas funcionalidades não devem ser consideradas existentes na versão atual.
