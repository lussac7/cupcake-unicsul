# Fase 11 --- Serviços e Regras de Negócio

## Projeto Cupcake Unicsul

**Status:** Concluída --- `mvn clean test` executado com
`BUILD SUCCESS`.

## 1. Objetivo

Implementar a lógica de aplicação dos módulos `product` e `order`,
respeitando o modelo de domínio, os DTOs e as regras de negócio
aprovadas nas fases anteriores.

## 2. ProductService

Arquivo:
`src/main/java/io/github/lussac7/cupcake/product/service/ProductService.java`

Responsabilidades: - Listar produtos ativos e consultar produto ativo
por identificador. - Cadastrar e atualizar produtos. - Desativar e
reativar produtos.

Decisões técnicas: - Injeção de dependência por construtor. -
`@Transactional(readOnly = true)` no nível da classe e `@Transactional`
nos métodos de escrita. - Conversão de entidades para
`ProductResponse`. - `NoSuchElementException` para produto inexistente
ou inativo na consulta pública. - Atualização, ativação e desativação
utilizam dirty checking do Hibernate; não chamam `save()` explicitamente
para entidades já gerenciadas.

Teste:
`src/test/java/io/github/lussac7/cupcake/product/service/ProductServiceTest.java`.

Os testes cobrem listagem, consulta, cadastro, atualização, ativação,
desativação e produto inexistente.

## 3. OrderService

Arquivo:
`src/main/java/io/github/lussac7/cupcake/order/service/OrderService.java`

Responsabilidades: - Criar pedidos e consultá-los por identificador. -
Validar existência e disponibilidade dos produtos. - Consolidar itens
repetidos. - Obter preços das entidades `Product` recuperadas do
banco. - Criar o agregado `Order` e seus itens e converter o pedido para
`OrderResponse`.

Decisões técnicas: - Utiliza `OrderRepository` e `ProductRepository`,
com injeção por construtor. - `@Transactional(readOnly = true)` no nível
da classe e `@Transactional` na criação. - `LinkedHashMap` consolida
quantidades de produtos repetidos e preserva a ordem de inserção. - O
serviço compara os identificadores solicitados com os produtos
encontrados. - O domínio `Order` cria `OrderItem` e recalcula o total. -
O agregado é persistido por `OrderRepository.save()`, aproveitando
`CascadeType.ALL`. - A conversão para DTO ocorre dentro da transação,
pois os relacionamentos utilizam carregamento LAZY. -
`NoSuchElementException` representa produto ou pedido não encontrado. -
`IllegalArgumentException` representa tentativa de incluir produto
inativo.

### Consolidação de itens

Exemplo: Produto 10 com quantidade 2, Produto 20 com quantidade 1 e
Produto 10 com quantidade 3 resultam em Produto 10 com quantidade 5 e
Produto 20 com quantidade 1.

## 4. Testes unitários do OrderService

Arquivo:
`src/test/java/io/github/lussac7/cupcake/order/service/OrderServiceTest.java`.

Os sete testes cobrem: 1. Criação de pedido. 2. Consolidação de produtos
repetidos. 3. Rejeição de produto inexistente. 4. Rejeição de produto
inativo. 5. Utilização do preço do banco de dados. 6. Consulta de pedido
por identificador. 7. Rejeição de pedido inexistente.

Os testes usam JUnit 5 e Mockito, sem conexão com banco de dados.

### Ajuste realizado

O Mockito identificou configurações não utilizadas no teste de produto
inativo (`UnnecessaryStubbingException`). O método auxiliar
`createProduct()` foi ajustado com `lenient()` nas configurações de nome
e preço, que não são utilizadas nesse cenário. Após o ajuste, a suíte
passou.

## 5. Transações

  Operação                    Limite transacional
  --------------------------- ----------------------
  Consultar produtos ativos   Somente leitura
  Consultar produto ativo     Somente leitura
  Cadastrar produto           Transação de escrita
  Atualizar produto           Transação de escrita
  Ativar/desativar produto    Transação de escrita
  Criar pedido                Transação de escrita
  Consultar pedido            Somente leitura

A criação do pedido e de seus itens ocorre na mesma transação. Uma falha
durante a persistência provoca rollback.

## 6. Exceções

As exceções utilizadas nesta fase são provisórias: -
`NoSuchElementException`: produto ou pedido não encontrado. -
`IllegalArgumentException`: produto inativo ou violação de regra de
domínio. - `IllegalStateException`: pedido sem itens, conforme validação
do agregado.

O mapeamento dessas exceções para respostas HTTP será tratado na fase da
camada web/API.

## 7. Resultado

-   `ProductService` implementado e testado.
-   `OrderService` implementado e testado.
-   Nenhuma alteração necessária nas entidades `Product`, `Order` e
    `OrderItem` ou nos repositórios existentes.
-   Build final confirmado: **`BUILD SUCCESS`**.

## 8. Arquivos principais

``` text
src/main/java/io/github/lussac7/cupcake/
├── product/service/ProductService.java
└── order/service/OrderService.java

src/test/java/io/github/lussac7/cupcake/
├── product/service/ProductServiceTest.java
└── order/service/OrderServiceTest.java
```

## 9. Versionamento sugerido

``` bash
git add src/main src/test
git commit -m "feat: implement product and order services"
```

Adicionar este documento à pasta `docs/` do repositório, caso ela esteja
sendo utilizada para a documentação das fases.
