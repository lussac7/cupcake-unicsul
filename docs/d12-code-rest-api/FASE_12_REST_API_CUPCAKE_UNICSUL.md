# Fase 12 — REST API
## Projeto Cupcake Unicsul

**Status:** Concluída  
**Stack:** Java 21, Spring Boot 4.1.2, Spring MVC, Spring Data JPA, Maven  
**Base da API:** `/api/v1`

## 1. Objetivo

Disponibilizar os casos de uso de produtos e pedidos por meio de uma API REST. Os controllers recebem e retornam DTOs e delegam as operações aos serviços da aplicação.

## 2. Endpoints implementados

### Produtos

| Método | Endpoint | Comportamento | Sucesso |
|---|---|---|---|
| GET | `/api/v1/products` | Lista produtos ativos, paginados e ordenados | 200 OK |
| GET | `/api/v1/products/{id}` | Consulta produto ativo por ID | 200 OK |
| POST | `/api/v1/products` | Cadastra produto | 201 Created |
| PUT | `/api/v1/products/{id}` | Atualiza produto | 200 OK |
| DELETE | `/api/v1/products/{id}` | Desativa produto logicamente | 204 No Content |

Paginação: `page`, `size` e `sort`. Padrão: `page=0`, `size=20`, `sort=price,asc`.

Produtos inativos não aparecem nas consultas públicas. A desativação lógica preserva o histórico dos pedidos.

### Pedidos

| Método | Endpoint | Comportamento | Sucesso |
|---|---|---|---|
| POST | `/api/v1/orders` | Cria pedido | 201 Created |
| GET | `/api/v1/orders/{id}` | Consulta pedido por ID | 200 OK |
| GET | `/api/v1/orders` | Lista pedidos paginados e ordenados | 200 OK |

Paginação: `page`, `size` e `sort`. Padrão: `page=0`, `size=20`, `sort=createdAt,desc`.

A criação de produto e de pedido retorna o cabeçalho `Location` com o endereço do recurso criado.

## 3. Implementação

### ProductController

`src/main/java/io/github/lussac7/cupcake/product/controller/ProductController.java`

- Expõe os endpoints REST de produtos.
- Valida DTOs de entrada com `@Valid`.
- Delega operações ao `ProductService`.
- Implementa paginação e ordenação.
- Retorna os códigos HTTP adequados.

### OrderController

`src/main/java/io/github/lussac7/cupcake/order/controller/OrderController.java`

- Expõe criação e consulta de pedidos.
- Valida `OrderCreateRequest`.
- Delega operações ao `OrderService`.
- Implementa listagem paginada, ordenada por `createdAt` decrescente por padrão.

### Serviços e repositórios

- `ProductService` executa operações de produtos e converte resultados para DTOs.
- `OrderService` cria e consulta pedidos, aplica regras de negócio e converte resultados para DTOs.
- `ProductRepository` oferece consultas de produtos ativos com e sem paginação.
- `OrderRepository` fornece a consulta paginada por `findAll(Pageable)`.

## 4. Validação e códigos HTTP

Os endpoints de criação e atualização utilizam validação dos DTOs com Bean Validation e `@Valid`.

Códigos previstos no desenho da API:

- `200 OK`: consulta ou atualização concluída.
- `201 Created`: recurso criado.
- `204 No Content`: produto desativado.
- `400 Bad Request`: dados inválidos.
- `404 Not Found`: recurso não encontrado.
- `409 Conflict`: conflito de negócio, quando aplicável.
- `500 Internal Server Error`: erro inesperado.

O tratamento padronizado de erros com Problem Details será implementado na Fase 13.

## 5. Testes

Os testes web utilizam `@WebMvcTest` e `@MockitoBean`, isolando os controllers dos serviços reais.

Foi adicionada a dependência `spring-boot-starter-webmvc-test` com escopo `test`, necessária à configuração de testes web no Spring Boot 4.

### ProductControllerTest

- Listagem e consulta por identificador.
- Cadastro e rejeição de requisição inválida.
- Atualização e desativação.
- Parâmetros de paginação e ordenação.

### OrderControllerTest

- Criação e rejeição de requisição inválida.
- Consulta por identificador.
- Listagem paginada.
- Parâmetros de paginação e ordenação.
- Paginação e ordenação padrão.

No teste de página solicitada, o mock retorna `Page.empty(PageRequest.of(...))` com o mesmo `Pageable` esperado. `Page.empty()` sem argumentos representa uma página vazia padrão, com número e tamanho diferentes dos parâmetros enviados na requisição.

**Validação final:** `mvn clean test` executado pelo usuário com resultado `BUILD SUCCESS`.

## 6. Decisões e limitações

- A API permanece sem autenticação, conforme o escopo acadêmico aprovado.
- Os endpoints administrativos de produtos e consulta de pedidos também permanecem sem autenticação.
- Consultar pedidos por identificador sem autenticação é uma limitação acadêmica e não deve ser considerado adequado para uma loja em produção.
- Os controllers utilizam DTOs; entidades JPA não são expostas diretamente.
- A paginação é retornada atualmente usando `Page` do Spring Data.

### Observação sobre Page

O Spring Data pode emitir um aviso de que serializar diretamente `PageImpl` não é recomendado como contrato estável de API. Esse aviso não causou falha nos testes.

Uma alternativa futura é criar um DTO próprio de paginação, controlando explicitamente o JSON. Essa alteração não foi incluída nesta fase e deve ser avaliada separadamente.

## 7. Critérios de conclusão

- [x] Endpoints REST de produtos implementados.
- [x] Endpoints REST de pedidos implementados.
- [x] Validação dos DTOs na camada web.
- [x] Códigos HTTP de sucesso definidos.
- [x] Cabeçalho `Location` na criação.
- [x] Paginação de produtos e pedidos.
- [x] Ordenação padrão de produtos por preço crescente.
- [x] Ordenação padrão de pedidos por data de criação decrescente.
- [x] Testes web dos controllers.
- [x] `mvn clean test` concluído com `BUILD SUCCESS`.

## 8. Próxima fase

**Fase 13 — Tratamento padronizado de erros com Problem Details.**

O objetivo será padronizar respostas HTTP para erros de validação, recursos inexistentes e conflitos de negócio.

## 9. Commit sugerido

```bash
git add src/main src/test docs
git commit -m "feat: expose product and order REST API"
git push
```
