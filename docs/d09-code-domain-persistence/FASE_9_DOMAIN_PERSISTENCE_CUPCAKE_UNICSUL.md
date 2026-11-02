# Fase 9 — Domain e Persistence
## Projeto Cupcake Unicsul

**Status:** Concluída  
**Stack:** Java 21, Spring Boot 4.1.2, Spring Data JPA, Hibernate, PostgreSQL, JUnit 5

---

## 1. Objetivo

Implementar as entidades de domínio e os repositórios de persistência dos módulos `product` e `order`, respeitando o modelo de domínio e o esquema de banco de dados aprovados nas fases anteriores.

A implementação foi incremental: cada entidade foi acompanhada de suas regras de domínio, repositório e testes.

## 2. Módulo Product

### 2.1 Entidade `Product`

Arquivo:

`src/main/java/io/github/lussac7/cupcake/product/entity/Product.java`

A entidade representa um produto do catálogo e contém:

- `id`
- `name`
- `description`
- `price`
- `imageUrl`
- `active`
- `createdAt`
- `updatedAt`

Comportamentos implementados:

- criação com validação dos dados;
- atualização dos detalhes do produto;
- desativação;
- reativação.

A entidade não oferece setters públicos. A atualização dos detalhes valida os valores antes de modificar os atributos, evitando alteração parcial em caso de falha de validação.

### 2.2 Repositório `ProductRepository`

Arquivo:

`src/main/java/io/github/lussac7/cupcake/product/repository/ProductRepository.java`

Estende `JpaRepository<Product, Long>` e define:

```java
List<Product> findAllByActiveTrue();
```

Esse método permite consultar somente os produtos ativos.

### 2.3 Testes

Arquivos:

- `src/test/java/io/github/lussac7/cupcake/product/entity/ProductTest.java`
- `src/test/java/io/github/lussac7/cupcake/product/repository/ProductRepositoryTest.java`

Os testes verificam criação, validações, imagem opcional, atualização, desativação, reativação e consulta de produtos ativos.

## 3. Módulo Order

### 3.1 Entidade `Order`

Arquivo:

`src/main/java/io/github/lussac7/cupcake/order/entity/Order.java`

`Order` é a raiz do agregado de pedidos.

Atributos:

- `id`
- `customerName`
- `customerEmail`
- `deliveryAddress`
- `createdAt`
- `total`
- `items`

Comportamentos implementados:

- criação com validação dos dados do cliente;
- adição de itens;
- rejeição de produtos inativos;
- prevenção da inclusão duplicada do mesmo produto;
- recálculo do total;
- rejeição da persistência de pedidos sem itens.

A coleção de itens é mantida internamente. O método `getItems()` retorna uma cópia imutável da lista.

### 3.2 Entidade `OrderItem`

Arquivo:

`src/main/java/io/github/lussac7/cupcake/order/entity/OrderItem.java`

Cada item contém:

- `id`
- referência ao `Order`;
- referência ao `Product`;
- `productName`;
- `unitPrice`;
- `quantity`;
- `subtotal`.

O nome e o preço são copiados do produto no momento em que o item é criado. Esses valores constituem o snapshot histórico do pedido.

O subtotal é calculado por:

```text
subtotal = unitPrice × quantity
```

### 3.3 Relacionamentos JPA

- `Order` → `OrderItem`: `@OneToMany`, com `cascade = CascadeType.ALL`, `orphanRemoval = true` e carregamento `LAZY`.
- `OrderItem` → `Order`: `@ManyToOne`, com carregamento `LAZY`.
- `OrderItem` → `Product`: `@ManyToOne`, com carregamento `LAZY`.
- O relacionamento com `Product` não utiliza cascade.

A estrutura corresponde às chaves estrangeiras definidas na migration V1.

### 3.4 Repositório `OrderRepository`

Arquivo:

`src/main/java/io/github/lussac7/cupcake/order/repository/OrderRepository.java`

Estende `JpaRepository<Order, Long>`, utilizando as operações padrão de persistência e consulta. Não foram adicionados métodos específicos sem necessidade concreta.

## 4. Testes do agregado Order

### 4.1 Testes de domínio

Arquivo:

`src/test/java/io/github/lussac7/cupcake/order/entity/OrderTest.java`

Os testes verificam:

- criação de pedido com dados válidos;
- rejeição de nome, e-mail ou endereço vazios;
- rejeição de produto nulo;
- rejeição de produto inativo;
- rejeição de quantidade zero ou negativa;
- cálculo de subtotal e total;
- prevenção de produto duplicado;
- preservação do snapshot após atualização do produto.

### 4.2 Testes de persistência

Arquivo:

`src/test/java/io/github/lussac7/cupcake/order/repository/OrderRepositoryTest.java`

Os testes verificam:

- persistência de pedido com itens;
- geração dos identificadores;
- persistência em cascata dos itens;
- referências entre pedido, itens e produto;
- cálculo do subtotal e do total;
- rejeição da persistência de pedido sem itens.

## 5. Verificação

Os testes foram executados com:

```bash
mvn clean test
```

**Resultado informado pelo usuário: `BUILD SUCCESS`.**

A confirmação foi obtida após os testes do módulo Product, dos testes de domínio de Order e dos testes de persistência de Order.

## 6. Decisões preservadas

- O carrinho continua sendo mantido no navegador, conforme definido nas fases anteriores.
- Não foi criada uma entidade `Customer`.
- O endereço de entrega permanece como um único campo textual.
- O pedido preserva o nome e o preço dos produtos no momento da compra.
- Produtos são desativados logicamente, não removidos fisicamente.
- O serviço de checkout deverá consolidar quantidades repetidas antes de adicionar itens ao pedido, pois o modelo e a restrição do banco não permitem dois itens do mesmo produto no mesmo pedido.
- Não foram introduzidos microsserviços, abstrações genéricas ou padrões adicionais desnecessários.

## 7. Resultado da fase

A Fase 9 está concluída. Os módulos `product` e `order` possuem suas entidades, repositórios e testes correspondentes.

A próxima fase deverá seguir a sequência definida no plano do projeto, sem antecipar a implementação dos serviços de aplicação ou dos controladores REST.
