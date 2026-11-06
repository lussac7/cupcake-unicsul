# Arquitetura do Sistema

## 1. Visão geral

O Cupcake é uma aplicação web acadêmica para uma loja de cupcakes, desenvolvida com Java 21 e Spring Boot 4.1.2.

O backend utiliza uma arquitetura de Monólito Modular (*Modular Monolith*) combinada com Arquitetura em Camadas (*Layered Architecture*).

A aplicação é organizada em módulos funcionais, com responsabilidades separadas e dependências estruturadas para facilitar a manutenção, os testes e a evolução do sistema.

### Tecnologias principais

**Backend**

- Java 21
- Spring Boot 4.1.2
- Maven Wrapper
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- JUnit 6
- Mockito
- Testcontainers
- H2 para testes

**Frontend**

- HTML5
- CSS3
- JavaScript puro (Vanilla JavaScript)
- JavaScript ES Modules
- Fetch API
- localStorage

**Controle de versão**

- Git

## 2. Estilo arquitetural

O sistema adota dois conceitos arquiteturais complementares:

- **Monólito Modular:** a aplicação é implantada como uma única unidade, mas seu código é organizado em módulos funcionais.
- **Arquitetura em Camadas:** cada módulo separa responsabilidades de apresentação, serviços, persistência e domínio.

Essa organização busca manter o código simples, coeso e de fácil compreensão, sem introduzir a complexidade operacional de uma arquitetura de microsserviços.

## 3. Organização dos módulos

O código-fonte está organizado nos seguintes módulos:

```text
io.github.lussac7.cupcake
├── CupcakeApplication.java
├── product
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── order
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
└── shared
    └── exception
```

### 3.1 Módulo Product

Responsável pelo gerenciamento dos produtos da loja.

Principais responsabilidades:

- Criar produtos.
- Consultar produtos ativos.
- Consultar um produto pelo identificador.
- Atualizar informações dos produtos.
- Desativar produtos.

### 3.2 Módulo Order

Responsável pelo gerenciamento dos pedidos.

Principais responsabilidades:

- Criar pedidos.
- Consultar pedidos pelo identificador.
- Listar pedidos de forma paginada.
- Calcular subtotais e totais.
- Preservar o nome e o preço dos produtos no momento da compra.

### 3.3 Módulo Shared

Contém componentes compartilhados pelos módulos funcionais.

Atualmente, inclui o tratamento centralizado de exceções:

- `BusinessRuleException`
- `ResourceNotFoundException`
- `GlobalExceptionHandler`

## 4. Arquitetura em camadas

Cada módulo funcional utiliza as seguintes camadas:

### 4.1 Camada de apresentação — Controller

Responsável pela comunicação HTTP com os clientes da API.

Principais responsabilidades:

- Receber requisições HTTP.
- Validar os dados de entrada por meio dos DTOs.
- Acionar os serviços correspondentes.
- Retornar respostas HTTP apropriadas.

Os controllers não devem expor diretamente as entidades JPA.

### 4.2 Camada de serviço — Service

Responsável pela execução dos casos de uso e pela coordenação das regras de negócio.

Principais responsabilidades:

- Executar operações do sistema.
- Aplicar regras de negócio.
- Coordenar o acesso aos repositórios.
- Controlar operações transacionais.
- Converter entidades em objetos de resposta quando necessário.

### 4.3 Camada de persistência — Repository

Responsável pelo acesso aos dados.

Utiliza Spring Data JPA para abstrair as operações de persistência.

Principais responsabilidades:

- Consultar registros.
- Persistir entidades.
- Atualizar registros.
- Implementar consultas específicas necessárias aos módulos.

### 4.4 Camada de domínio — Entity

Representa os conceitos fundamentais do domínio da aplicação.

As entidades concentram atributos, estado e regras de negócio relacionadas aos respectivos objetos.

Entidades atualmente implementadas:

- `Product`
- `Order`
- `OrderItem`

As entidades JPA não são utilizadas diretamente como contratos da API REST. Os DTOs são utilizados na fronteira entre a aplicação e os clientes HTTP.

## 5. Comunicação entre as camadas

O fluxo típico de uma requisição é:

```text
Cliente HTTP
     |
     v
Controller
     |
     v
Service
     |
     v
Repository
     |
     v
Banco de dados
```

As respostas percorrem o caminho inverso, retornando ao cliente por meio dos DTOs de resposta.

## 6. DTOs — Data Transfer Objects

Os DTOs representam os dados recebidos e enviados pela API.

Essa abordagem permite separar o modelo de persistência dos contratos HTTP.

### Product

- `ProductCreateRequest`
- `ProductUpdateRequest`
- `ProductResponse`

### Order

- `OrderCreateRequest`
- `OrderItemRequest`
- `OrderResponse`
- `OrderItemResponse`

As validações dos dados de entrada utilizam as anotações do Jakarta Bean Validation.

## 7. Tratamento de exceções

O módulo `shared` centraliza o tratamento de exceções da aplicação.

O `GlobalExceptionHandler` utiliza `ProblemDetail` para estruturar respostas de erro HTTP.

Mapeamentos principais:

| Exceção ou situação | Status HTTP |
|---|---:|
| Recurso não encontrado | 404 |
| Regra de negócio violada | 409 |
| Erro de validação ou requisição inválida | 400 |
| Erro inesperado | 500 |

O tratamento centralizado evita a repetição de lógica de tratamento de erros nos controllers.

## 8. Persistência e banco de dados

O sistema utiliza PostgreSQL como banco de dados principal.

O acesso aos dados é realizado por meio de Spring Data JPA e Hibernate.

O Flyway gerencia as alterações do esquema do banco de dados por meio de migrações versionadas.

A migração inicial está localizada em:

```text
src/main/resources/db/migration/
└── V1__initial_schema.sql
```

O Hibernate utiliza a configuração `ddl-auto: validate`, verificando a compatibilidade entre as entidades e o esquema existente, sem assumir a responsabilidade de criar ou alterar o esquema em produção.

## 9. Configurações por ambiente

A aplicação utiliza arquivos de configuração específicos para os diferentes ambientes.

### application.yml

Contém configurações gerais da aplicação, incluindo:

- Nome da aplicação.
- Configuração do Hibernate.
- Configuração do Flyway.
- Nível de logging padrão.
- Desativação do Open Session in View.

### application-dev.yml

Configuração para desenvolvimento local, utilizando PostgreSQL.

As credenciais são obtidas por meio das variáveis de ambiente:

- `DB_USERNAME`
- `DB_PASSWORD`

### application-test.yml

Configuração utilizada nos testes, com banco H2 em memória.

O Flyway está desabilitado nesse perfil, e o Hibernate utiliza `create-drop`.

## 10. Estratégia de testes

O projeto utiliza diferentes níveis de testes:

- Testes unitários para regras de negócio.
- Testes de serviços com Mockito.
- Testes de repositórios.
- Testes de integração com PostgreSQL por meio de Testcontainers.
- Testes de controllers e componentes web.

O objetivo é verificar o comportamento dos componentes isoladamente e a integração entre as partes da aplicação.

## 11. Arquitetura do frontend

O projeto possui um frontend independente, desenvolvido com HTML5, CSS3 e JavaScript puro (Vanilla JavaScript), organizado em módulos ES.

O frontend é responsável pela interface de interação com o usuário e consome os serviços disponibilizados pela API REST do backend.

### 11.1 Organização dos arquivos

O código-fonte está organizado nos seguintes diretórios:

```text
frontend/
├── assets/
│   └── images/
├── css/
│   ├── components.css
│   └── styles.css
├── js/
│   ├── api/
│   │   ├── http-client.js
│   │   ├── order-api.js
│   │   └── product-api.js
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── utils/
│   └── app.js
└── index.html
```

Responsabilidades principais:

- `api`: comunicação HTTP com o backend.
- `components`: componentes reutilizáveis da interface.
- `pages`: implementação das páginas da aplicação.
- `services`: serviços utilizados pelo frontend, incluindo gerenciamento do carrinho.
- `utils`: funções auxiliares e validações.
- `app.js`: inicialização e gerenciamento da navegação.
- `css`: estilos gerais e componentes visuais.
- `assets`: imagens e demais recursos estáticos.

### 11.2 Navegação

A aplicação utiliza rotas Hash para navegação entre as páginas.

As rotas implementadas são:

| Rota | Funcionalidade |
|---|---|
| `#/products` | Catálogo de produtos |
| `#/products/{id}` | Detalhes do produto |
| `#/cart` | Carrinho de compras |
| `#/checkout` | Finalização do pedido |
| `#/orders/{id}` | Consulta do pedido |

O carregamento das páginas utiliza importação dinâmica de módulos JavaScript (`import()`).

### 11.3 Comunicação com o backend

A comunicação ocorre por meio da Fetch API, utilizando os módulos responsáveis pelo acesso aos recursos de produtos e pedidos.

O backend disponibiliza a API REST na porta `8080`.

O frontend é servido independentemente, na porta `5500`, conforme a configuração CORS do backend.

### 11.4 Gerenciamento do carrinho

O carrinho de compras é gerenciado pelo serviço `cart-service.js`.

Os dados são armazenados no `localStorage` do navegador, utilizando a chave:

```text
cupcake.cart
```

O armazenamento local mantém os identificadores dos produtos e suas quantidades.

Os valores monetários são obtidos e calculados pelo backend durante a criação do pedido, que permanece como fonte da verdade para os preços e totais.

### 11.5 Responsabilidades arquiteturais

O frontend e o backend possuem responsabilidades separadas:

- O frontend gerencia a apresentação, a navegação e a interação com o usuário.
- O backend executa as regras de negócio, valida os pedidos e calcula os valores financeiros.
- A API REST estabelece o contrato de comunicação entre as duas partes.

Essa separação permite que a interface evolua independentemente da implementação interna do backend.

## 12. Limitações atuais

A implementação atual possui as seguintes limitações funcionais:

- Não existe autenticação de usuários.
- Não existe autorização baseada em perfis ou permissões.
- Não existe integração com serviços de pagamento.
- Não existe upload de imagens.
- Não existe gerenciamento de status ou cancelamento de pedidos.
- Não existe controle de titularidade no acesso aos pedidos.
- O carrinho de compras é armazenado localmente no navegador, sem persistência no backend.

Em particular, os endpoints de consulta de pedidos retornam dados do cliente, incluindo e-mail e endereço, sem verificar a identidade ou a titularidade da requisição. Isso deve ser considerado antes de disponibilizar a aplicação em um ambiente público de produção.

## 13. Considerações finais

A arquitetura atual prioriza simplicidade, separação de responsabilidades e facilidade de manutenção.

A organização modular permite que novas funcionalidades sejam incorporadas sem abandonar a estrutura existente.

A implementação deve continuar evoluindo de forma incremental, preservando a coerência entre o código-fonte, os testes e a documentação.
