# Estratégia de Testes

## 1. Visão geral

O projeto Cupcake utiliza testes automatizados para verificar o comportamento das regras de negócio, dos serviços, dos repositórios e da integração com o banco de dados.

A estratégia prioriza a confiabilidade funcional e a verificação das regras de negócio, evitando depender exclusivamente de métricas quantitativas de cobertura de código.

As principais ferramentas utilizadas são:

- JUnit 5;
- Mockito;
- Spring Boot Test;
- Spring Data JPA Test;
- Spring Web MVC Test;
- H2;
- Testcontainers;
- PostgreSQL.

## 2. Objetivos da estratégia

A estratégia de testes tem os seguintes objetivos:

- Verificar o comportamento esperado das funcionalidades.
- Validar as regras de negócio das entidades.
- Verificar a implementação dos serviços.
- Validar as operações de persistência.
- Verificar a integração com PostgreSQL.
- Identificar regressões durante a evolução do código.
- Preservar a integridade dos dados.

A prioridade é testar comportamentos e resultados observáveis, especialmente aqueles relacionados às regras de negócio.

## 3. Tipos de testes

### 3.1 Testes unitários

Os testes unitários verificam componentes isoladamente, sem depender de infraestrutura externa.

São utilizados principalmente para validar:

- Regras de negócio das entidades.
- Validações de atributos.
- Transições de estado.
- Tratamento de condições inválidas.
- Comportamentos específicos dos métodos.

O JUnit 5 é utilizado para estruturar e executar os testes.

### 3.2 Testes de serviços

Os testes de serviços verificam os casos de uso da aplicação.

O Mockito é utilizado para simular dependências, permitindo testar os serviços sem depender diretamente do banco de dados.

Entre os comportamentos importantes verificados estão:

- Criação de produtos.
- Atualização de produtos.
- Desativação de produtos.
- Consulta de produtos ativos.
- Criação de pedidos.
- Validação dos produtos associados aos pedidos.
- Cálculo de subtotais e totais.
- Preservação dos dados históricos dos pedidos.
- Tratamento de recursos inexistentes.

Os testes também verificam se os serviços respeitam as regras de negócio antes de persistir alterações.

### 3.3 Testes de repositórios

Os testes de repositórios verificam as operações de persistência e as consultas implementadas com Spring Data JPA.

São utilizados para validar:

- Consultas personalizadas.
- Filtragem de produtos ativos.
- Paginação.
- Ordenação.
- Recuperação de registros.
- Comportamento das operações de persistência.

Esses testes ajudam a identificar problemas relacionados ao mapeamento JPA e às consultas utilizadas pela aplicação.

### 3.4 Testes de integração

Os testes de integração verificam o funcionamento conjunto de diferentes componentes da aplicação.

O projeto utiliza Testcontainers para executar testes com uma instância real do PostgreSQL.

A imagem utilizada é:

```text
postgres:16-alpine
```

Essa abordagem permite verificar o comportamento da aplicação com um banco de dados compatível com o utilizado no ambiente principal.

Os testes de integração verificam aspectos como:

- Persistência de entidades.
- Relacionamentos entre entidades.
- Restrições do banco de dados.
- Consultas e operações de repositório.
- Integração entre os componentes da aplicação.
- Comportamento das operações envolvendo produtos e pedidos.

O Testcontainers gerencia a inicialização e o encerramento do container utilizado nos testes.

## 4. Bancos de dados utilizados nos testes

O projeto utiliza duas estratégias de banco de dados para testes.

### 4.1 H2

O H2 é utilizado nos testes que precisam de um banco de dados em memória.

Configuração:

```text
jdbc:h2:mem:cupcake_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
```

O perfil `test` utiliza:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

O banco é criado durante a execução dos testes e descartado ao final.

### 4.2 PostgreSQL com Testcontainers

Os testes de integração utilizam PostgreSQL em container Docker.

Essa estratégia é importante porque o H2, mesmo configurado em modo de compatibilidade PostgreSQL, não reproduz integralmente o comportamento do PostgreSQL.

O uso de Testcontainers permite testar a aplicação com o próprio mecanismo de banco de dados utilizado no ambiente principal.

## 5. Estratégia de validação das regras de negócio

As regras de negócio devem ser verificadas nos componentes responsáveis por implementá-las.

### 5.1 Produtos

Os testes devem verificar:

- Preço positivo.
- Validação dos dados obrigatórios.
- Atualização de informações.
- Desativação de produtos.
- Preservação do estado de ativação durante a atualização de detalhes.
- Ausência de alterações no estado do objeto quando uma atualização recebe dados inválidos.
- Exclusão de produtos inativos das consultas destinadas a produtos ativos.

### 5.2 Pedidos

Os testes devem verificar:

- Existência de pelo menos um item.
- Quantidade positiva.
- Existência dos produtos solicitados.
- Impossibilidade de incluir produtos inativos.
- Cálculo correto dos subtotais.
- Cálculo correto do total do pedido.
- Consolidação de produtos repetidos na requisição.
- Preservação do nome e do preço unitário no momento da compra.

Essas verificações são importantes para garantir a consistência dos pedidos e a preservação do histórico de compras.

## 6. Organização dos testes

Os testes estão organizados no diretório:

```text
src/test/java/
```

A organização acompanha os módulos e componentes da aplicação.

Os recursos de teste estão em:

```text
src/test/resources/
```

Atualmente, esse diretório contém:

```text
application-test.yml
```

A configuração específica dos testes é mantida separada das configurações utilizadas em desenvolvimento.

## 7. Resultados da estratégia de testes — Fase 15

Durante a Fase 15, foram adicionados quatro testes para reforçar a validação dos comportamentos de produtos.

| Classe | Teste | Objetivo |
|---|---|---|
| `ProductRepositoryTest` | `shouldFindOnlyActiveProductsWithPagination` | Verificar a consulta paginada de produtos ativos |
| `ProductRepositoryTest` | `shouldReturnCorrectPageWhenNavigatingActiveProducts` | Verificar a navegação entre páginas de produtos ativos |
| `ProductTest` | `shouldNotModifyProductWhenUpdateDetailsReceivesInvalidData` | Verificar que dados inválidos não modificam o estado do produto |
| `ProductTest` | `shouldPreserveInactiveStatusWhenUpdatingProduct` | Verificar que a atualização dos detalhes preserva o estado inativo |

Os quatro testes foram executados individualmente com sucesso.

Posteriormente, o usuário executou:

```bash
mvn clean test
```

O resultado foi confirmado como bem-sucedido.

### Inventário ao final da Fase 15

O inventário esperado após as alterações da Fase 15 era de:

| Indicador | Resultado |
|---|---:|
| Total de testes | 97 |
| Testes com falha | 0 |
| Testes com erro | 0 |
| Testes ignorados | 0 |
| Resultado do Maven | BUILD SUCCESS |

Esses números representam o resultado confirmado ao final da Fase 15. Alterações posteriores no código ou nos testes podem modificar esse inventário.

## 8. Cobertura de testes

O projeto não utiliza JaCoCo nem estabelece um percentual mínimo de cobertura de código.

A estratégia adotada prioriza a suficiência qualitativa dos testes.

Isso significa avaliar se os comportamentos importantes estão adequadamente verificados, considerando:

- Regras de negócio.
- Cenários válidos.
- Cenários inválidos.
- Condições de erro.
- Integridade dos dados.
- Comportamentos de persistência.
- Integração entre componentes.

Um percentual elevado de cobertura, isoladamente, não garante que as regras de negócio estejam corretamente implementadas.

## 9. Executar os testes

A execução dos testes é realizada pelo Maven.

### 9.1 Executar todos os testes

```bash
mvn clean test
```

Remove os arquivos de compilação anteriores e executa a suíte de testes.

### 9.2 Executar testes sem limpar o projeto

```bash
mvn test
```

Executa os testes sem remover previamente os arquivos de compilação.

### 9.3 Executar uma classe específica

Exemplo:

```bash
mvn test -Dtest=ProductTest
```

### 9.4 Executar vários testes específicos

Exemplo:

```bash
mvn test -Dtest=ProductTest,ProductRepositoryTest
```

### 9.5 Executar os testes de integração

Para executar uma classe específica de integração:

```bash
mvn test -Dtest=PostgreSqlIntegrationTest
```

A execução dos testes que utilizam Testcontainers exige um ambiente Docker funcional.

## 10. Relatórios de execução

O Maven Surefire gera relatórios de execução no diretório:

```text
target/surefire-reports/
```

Esses relatórios permitem consultar:

- Quantidade de testes executados.
- Testes aprovados.
- Testes com falha.
- Testes com erro.
- Testes ignorados.
- Detalhes das falhas ocorridas.

Os relatórios podem ser utilizados para investigar problemas identificados durante a execução da suíte.

## 11. Recomendações para novos testes

Toda nova funcionalidade deve incluir testes compatíveis com seu comportamento e suas responsabilidades.

Recomendações:

1. Testar os cenários de sucesso.
2. Testar entradas inválidas.
3. Verificar as regras de negócio.
4. Testar condições de erro.
5. Verificar a persistência quando aplicável.
6. Utilizar Mockito para isolar dependências nos testes unitários.
7. Utilizar PostgreSQL com Testcontainers quando a compatibilidade com o banco real for relevante.
8. Executar a suíte completa antes de concluir uma alteração.

Os testes devem verificar comportamentos observáveis, evitando dependência des
