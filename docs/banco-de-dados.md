# Documentação do Banco de Dados

## 1. Visão geral

O projeto Cupcake utiliza PostgreSQL como sistema gerenciador de banco de dados.

A persistência é implementada com:

- PostgreSQL;
- Spring Data JPA;
- Hibernate;
- Flyway.

O Hibernate é responsável pelo mapeamento objeto-relacional (ORM), enquanto o Flyway controla a evolução do esquema do banco de dados por meio de migrações versionadas.

A configuração principal utiliza:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

O Hibernate valida a compatibilidade entre as entidades JPA e o esquema existente. A criação e a evolução do esquema são responsabilidade das migrações Flyway.

## 2. Modelo de dados

O banco de dados possui três tabelas principais:

| Tabela | Responsabilidade |
|---|---|
| `products` | Armazenar os produtos disponíveis na loja |
| `orders` | Armazenar os pedidos realizados pelos clientes |
| `order_items` | Armazenar os produtos e suas quantidades em cada pedido |

### 2.1 Relacionamentos

O relacionamento entre as tabelas é:

- Um pedido possui um ou mais itens.
- Cada item pertence a um pedido.
- Cada item referencia um produto cadastrado.

Representação simplificada:

```text
products
   |
   | 1
   |
   | N
order_items
   |
   | N
   |
   | 1
orders
```

A tabela `order_items` representa os itens associados aos pedidos e mantém informações históricas sobre os produtos adquiridos.

## 3. Estrutura das tabelas

### 3.1 Tabela products

Armazena os produtos comercializados pela loja.

| Coluna | Tipo SQL | Descrição |
|---|---|---|
| `id` | Conforme definido na migração | Identificador do produto e chave primária |
| `name` | VARCHAR(150) | Nome do produto |
| `description` | TEXT | Descrição do produto |
| `price` | NUMERIC(10,2) | Preço do produto |
| `image_url` | VARCHAR(500) | URL opcional da imagem |
| `active` | BOOLEAN | Indica se o produto está ativo |
| `created_at` | TIMESTAMPTZ | Data e hora de criação |
| `updated_at` | TIMESTAMPTZ | Data e hora da última atualização |

Restrições:

- Chave primária em `id`.
- Nome não pode ser vazio.
- Descrição não pode ser vazia.
- Preço deve ser maior que zero.
- `active` possui valor padrão `TRUE`.

O campo `active` permite desativar produtos sem removê-los fisicamente do banco de dados.

### 3.2 Tabela orders

Armazena os pedidos realizados pelos clientes.

| Coluna | Tipo SQL | Descrição |
|---|---|---|
| `id` | Conforme definido na migração | Identificador do pedido e chave primária |
| `customer_name` | VARCHAR(150) | Nome do cliente |
| `customer_email` | VARCHAR(254) | E-mail do cliente |
| `delivery_address` | TEXT | Endereço de entrega |
| `created_at` | TIMESTAMPTZ | Data e hora de criação |
| `total` | NUMERIC(12,2) | Valor total do pedido |

Restrições:

- Chave primária em `id`.
- Nome do cliente não pode ser vazio.
- E-mail não pode ser vazio.
- Endereço não pode ser vazio.
- Total deve ser maior que zero.

### 3.3 Tabela order_items

Armazena os itens associados a cada pedido.

| Coluna | Tipo SQL | Descrição |
|---|---|---|
| `id` | Conforme definido na migração | Identificador do item e chave primária |
| `order_id` | Conforme definido na migração | Referência ao pedido |
| `product_id` | Conforme definido na migração | Referência ao produto |
| `product_name` | VARCHAR(150) | Nome do produto no momento da compra |
| `unit_price` | NUMERIC(10,2) | Preço unitário no momento da compra |
| `quantity` | INTEGER | Quantidade adquirida |
| `subtotal` | NUMERIC(12,2) | Valor total do item |

Restrições:

- Chave primária em `id`.
- Chave estrangeira `order_id` referenciando `orders`.
- Chave estrangeira `product_id` referenciando `products`.
- Nome do produto não pode ser vazio.
- Preço unitário deve ser maior que zero.
- Quantidade deve ser maior que zero.
- Subtotal deve corresponder ao preço unitário multiplicado pela quantidade.
- A combinação `order_id` e `product_id` deve ser única.

As chaves estrangeiras utilizam `ON DELETE RESTRICT`, impedindo a exclusão física de registros referenciados.

## 4. Preservação do histórico de compras

A tabela `order_items` armazena o nome e o preço unitário do produto no momento da compra.

Essa decisão preserva o histórico do pedido mesmo quando o cadastro original do produto é atualizado posteriormente.

Por exemplo:

1. Um produto é vendido por R$ 12,50.
2. Um cliente realiza um pedido contendo esse produto.
3. Posteriormente, o preço do produto é alterado para R$ 15,00.
4. O pedido anterior continua registrando o preço de R$ 12,50.

Assim, o histórico financeiro do pedido não depende dos valores atuais do cadastro de produtos.

## 5. Índices

A migração inicial cria os seguintes índices:

| Índice | Tabela | Finalidade |
|---|---|---|
| `idx_products_active_name` | `products` | Otimizar consultas de produtos ativos por nome |
| `idx_order_items_product_id` | `order_items` | Otimizar consultas relacionadas aos produtos dos pedidos |
| `idx_orders_created_at` | `orders` | Otimizar consultas e ordenações por data de criação |

O índice `idx_products_active_name` é parcial e considera somente registros com `active = TRUE`.

## 6. Integridade dos dados

O banco de dados implementa restrições para proteger a integridade dos registros.

Entre as principais regras:

- Produtos devem possuir nome e descrição válidos.
- Preços devem ser positivos.
- Pedidos devem possuir informações válidas do cliente.
- O total do pedido deve ser positivo.
- Itens devem possuir quantidade positiva.
- O subtotal deve corresponder ao preço unitário multiplicado pela quantidade.
- Um mesmo produto não pode aparecer duplicado em um mesmo pedido.
- Registros referenciados não podem ser excluídos fisicamente enquanto houver referências.

Essas restrições complementam as validações realizadas pela aplicação.

## 7. Migrações com Flyway

O Flyway controla a evolução do esquema do banco de dados.

As migrações ficam armazenadas em:

```text
src/main/resources/db/migration/
```

A migração inicial é:

```text
V1__initial_schema.sql
```

Ela cria as tabelas:

- `products`;
- `orders`;
- `order_items`.

Também define as chaves primárias, chaves estrangeiras, restrições de integridade e índices.

As alterações futuras no esquema devem ser implementadas por novas migrações versionadas, sem modificar migrações que já tenham sido aplicadas em ambientes compartilhados ou de produção.

## 8. Configuração por ambiente

### 8.1 Desenvolvimento

O perfil `dev` utiliza PostgreSQL.

Configuração padrão:

```text
jdbc:postgresql://localhost:5432/cupcake
```

As credenciais são obtidas por meio das variáveis de ambiente:

```text
DB_USERNAME
DB_PASSWORD
```

### 8.2 Testes

O perfil `test` utiliza H2 em memória.

Configuração:

```text
jdbc:h2:mem:cupcake_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
```

Nesse perfil:

- O banco é mantido em memória.
- O Hibernate utiliza `ddl-auto: create-drop`.
- O Flyway está desabilitado.

### 8.3 Testes de integração

Os testes de integração utilizam Testcontainers com a imagem:

```text
postgres:16-alpine
```

Isso permite executar testes contra uma instância real do PostgreSQL em ambiente controlado, reduzindo diferenças entre o comportamento observado nos testes e o banco de dados utilizado pela aplicação.

## 9. Considerações sobre o modelo atual

O modelo de dados atual contempla exclusivamente produtos e pedidos.

Não existem tabelas implementadas para:

- Usuários e autenticação.
- Endereços cadastrados independentemente dos pedidos.
- Carrinho de compras persistente.
- Pagamentos.
- Categorias de produtos.
- Estoque.
- Status ou histórico de alterações dos pedidos.

Essas funcionalidades não devem ser consideradas parte do esquema atual.

## 10. Considerações finais

O esquema atual foi projetado para atender às operações implementadas de gerenciamento de produtos e pedidos.

O uso combinado de restrições SQL, regras de negócio na aplicação e migrações versionadas contribui para a integridade e a evolução controlada dos dados.

Qualquer alteração futura no modelo deve manter a coerência entre:

- Entidades JPA.
- Repositórios.
- Regras de negócio.
- Migrações Flyway.
- Testes automatizados.
- Documentação técnica.
