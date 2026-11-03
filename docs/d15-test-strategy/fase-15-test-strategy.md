# Fase 15 --- Estratégia de Testes

## 1. Objetivo

Avaliar se a suíte de testes protege adequadamente as funcionalidades
implementadas na aplicação Cupcake-Unicsul, priorizando cobertura útil
em vez de quantidade de testes.

A avaliação seguiu a estratégia definida no
`MASTER_PROMPT_CUPCAKE_UNICSUL.md`: happy path, entradas inválidas,
limites, regras de negócio, persistência e concorrência quando
relevante. Não foram adicionados testes para exercitar detalhes internos
sem valor comportamental.

## 2. Escopo avaliado

A revisão considerou o inventário de testes da Fase 14, os repositórios
`ProductRepository` e `OrderRepository`, o `ProductService`, a entidade
`Product` e os respectivos testes apresentados durante a fase.

### 2.1 Repositórios

**ProductRepository**

O repositório possui duas consultas derivadas:

-   `findAllByActiveTrue()`
-   `findAllByActiveTrue(Pageable pageable)`

A consulta não paginada já era coberta por
`shouldFindOnlyActiveProducts`. Foram acrescentados dois testes:

-   `shouldFindOnlyActiveProductsWithPagination`: verifica filtragem de
    produtos ativos, tamanho da página e metadados de paginação.
-   `shouldReturnCorrectPageWhenNavigatingActiveProducts`: verifica a
    navegação para a segunda página, com ordenação explícita por ID.

**OrderRepository**

O repositório estende `JpaRepository` e não declara consultas
customizadas. Os testes existentes verificam persistência do pedido com
itens e rejeição de pedido sem itens. A persistência e a recuperação
também são exercitadas por testes de serviço e integração. Não foi
identificada necessidade de testes adicionais específicos para métodos
herdados de `JpaRepository`.

### 2.2 Entidade Product

Foram acrescentados dois testes em `ProductTest`:

-   `shouldNotModifyProductWhenUpdateDetailsReceivesInvalidData`:
    verifica que uma atualização inválida não modifica parcialmente os
    atributos da entidade.
-   `shouldPreserveInactiveStatusWhenUpdatingProduct`: verifica que a
    atualização dos detalhes não reativa um produto inativo.

Os dois testes foram executados com sucesso.

### 2.3 ProductService

A revisão dos testes e da implementação confirmou cobertura das
operações principais:

-   listagem de produtos ativos, incluindo paginação;
-   busca por ID e tratamento de produto inexistente ou inativo;
-   criação e atualização;
-   ativação e desativação;
-   tratamento de produto inexistente nas operações de atualização,
    ativação e desativação.

A implementação usa dirty checking nas operações de atualização e
mudança de estado, com transações de escrita nos métodos
correspondentes. A entidade implementa ativação e desativação
idempotentes. Não foi identificada uma lacuna que justificasse ampliar a
suíte nesse ponto.

## 3. Inventário e evolução da suíte

O inventário inicial da Fase 14 identificou 93 testes.

  Área                         Testes identificados inicialmente
  -------------------------- -----------------------------------
  Product Entity                                              10
  Product Service                                             11
  Product Controller                                           8
  Product Repository                                           1
  Order Entity                                                11
  Order Service                                                8
  Order Controller                                             6
  Order Repository                                             2
  DTO Validation                                              14
  Global Exception Handler                                     5
  PostgreSQL Integration                                      14
  **Total**                                               **93**

Durante a Fase 15 foram acrescentados:

  Área                                                       Testes adicionados
  -------------------------------------------------------- --------------------
  Product Repository --- paginação                                            2
  Product Entity --- integridade e preservação de estado                      2
  **Total adicional**                                                     **4**

**Total esperado após as alterações: 97 testes.**

Os quatro testes novos foram executados individualmente e passaram. O
total de 97 é uma contagem esperada com base no inventário inicial e nos
testes adicionados; não representa uma nova execução completa da suíte
após todas as alterações.

## 4. Decisões de escopo

-   Não foram adicionados testes para métodos herdados de
    `JpaRepository` sem comportamento customizado relevante.
-   Não foram adicionados testes separados para cada repetição de
    operações idempotentes de ativação e desativação.
-   A atualização de produtos inativos foi considerada permitida pela
    implementação atual; não se presumiu uma regra de negócio diferente.
-   Concorrência não foi aprofundada nesta fase. Para o escopo acadêmico
    simples do projeto, não foi identificada justificativa suficiente
    para introduzir testes de concorrência ou mecanismos como locking
    otimista.
-   Não foi executada medição de cobertura de linhas ou branches com
    JaCoCo. Portanto, esta avaliação é qualitativa e não afirma um
    percentual de cobertura.

## 5. Verificação final recomendada

Antes de iniciar a Fase 16, executar a suíte completa após as últimas
alterações:

``` bash
mvn clean test
```

O resultado esperado, caso nenhum outro teste tenha sido alterado, é:

-   97 testes;
-   0 falhas;
-   0 erros;
-   `BUILD SUCCESS`.

A Fase 14 havia sido concluída anteriormente com 93 testes, todos
aprovados. Os quatro testes adicionados na Fase 15 também passaram
individualmente.

## 6. Conclusão

A Fase 15 está concluída. A estratégia de testes foi revisada de acordo
com a complexidade e o objetivo acadêmico da aplicação. Foram
acrescentados apenas quatro testes que cobrem comportamentos relevantes
não verificados diretamente antes: paginação de produtos ativos,
navegação entre páginas, atomicidade lógica de atualização inválida e
preservação do estado inativo.

A suíte foi ampliada de 93 para 97 testes esperados, sem introduzir
frameworks ou complexidade adicional. A próxima fase prevista no plano
do projeto é a **Fase 16 --- Documentação**.
