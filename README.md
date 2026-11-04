# Cupcake

Aplicação web acadêmica para uma loja de cupcakes, desenvolvida como parte de um projeto de Engenharia de Software na Universidade Cruzeiro do Sul (UNICSUL), na disciplina PIT-II.

O projeto disponibiliza uma API REST para gerenciamento de produtos e pedidos, utilizando Java 21, Spring Boot 4.1.2 e PostgreSQL.

## 1. Tecnologias

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem de programação |
| Spring Boot 4.1.2 | Framework da aplicação |
| Spring Web MVC | API REST |
| Spring Data JPA | Persistência de dados |
| Hibernate | Mapeamento objeto-relacional |
| PostgreSQL | Banco de dados principal |
| Flyway | Migrações do banco de dados |
| Maven | Construção e gerenciamento de dependências |
| JUnit 5 | Testes automatizados |
| Mockito | Isolamento de dependências nos testes |
| Testcontainers | Testes de integração com PostgreSQL |
| H2 | Banco de dados em memória para testes |
| Git | Controle de versão |

## 2. Arquitetura

A aplicação utiliza uma arquitetura de **Monólito Modular (Modular Monolith) combinada com Arquitetura em Camadas (Layered Architecture)**.

O código-fonte está organizado em três módulos principais:

- `product` — gerenciamento de produtos.
- `order` — gerenciamento de pedidos.
- `shared` — componentes compartilhados, incluindo tratamento de exceções.

Cada módulo funcional separa suas responsabilidades em:

- Controller
- DTO
- Entity
- Repository
- Service

A aplicação utiliza DTOs na fronteira da API REST, evitando a exposição direta das entidades JPA.

## 3. Funcionalidades implementadas

### Produtos

- Cadastro de produtos.
- Consulta de produtos ativos.
- Consulta de produto por identificador.
- Atualização de informações.
- Desativação de produtos.
- Listagem paginada.
- Ordenação dos resultados.

### Pedidos

- Criação de pedidos.
- Consulta de pedido por identificador.
- Listagem paginada de pedidos.
- Cálculo dos subtotais dos itens.
- Cálculo do valor total do pedido.
- Preservação dos nomes e preços dos produtos no momento da compra.
- Consolidação de identificadores repetidos de produtos em uma requisição.

### Tratamento de erros

- Tratamento centralizado de exceções.
- Respostas HTTP utilizando Problem Details.
- Validação das requisições.
- Tratamento de recursos não encontrados.
- Tratamento de violações de regras de negócio.

## 4. Estrutura do projeto

```text
cupcake-unicsul/
├── docs/
│   └── uml/
├── frontend/
│   ├── assets/
│   ├── css/
│   ├── js/
│   └── index.html
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```

## 5. Pré-requisitos

Para executar a aplicação localmente, são necessários:

- JDK Java 21.
- Maven.
- PostgreSQL.
- Docker para os testes de integração que utilizam Testcontainers.

## 6. Configuração do banco de dados

O perfil de desenvolvimento utiliza PostgreSQL.

Endereço padrão:

```text
jdbc:postgresql://localhost:5432/cupcake
```

Configure as seguintes variáveis de ambiente:

```bash
export DB_USERNAME=cupcake_user
export DB_PASSWORD='sua_senha'
```

Crie o banco de dados e configure o usuário PostgreSQL antes de iniciar a aplicação.

O Flyway gerencia automaticamente as migrações pendentes durante a inicialização.

## 7. Executando a aplicação

Inicie a aplicação utilizando o perfil de desenvolvimento:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Como alternativa, gere o arquivo JAR executável:

```bash
mvn clean package
```

Execute o artefato gerado:

```bash
java -jar target/cupcake-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=dev
```

## 8. Executando os testes

Execute toda a suíte de testes:

```bash
mvn clean test
```

Execute uma classe específica:

```bash
mvn test -Dtest=ProductTest
```

Os testes de integração que utilizam Testcontainers exigem que o Docker esteja disponível.

Os relatórios de execução são gerados em:

```text
target/surefire-reports/
```

## 9. API REST

O caminho-base da API é:

```text
/api/v1
```

Principais recursos:

| Método HTTP | Endpoint | Descrição |
|---|---|---|
| GET | `/products` | Listar produtos ativos |
| GET | `/products/{id}` | Consultar um produto |
| POST | `/products` | Cadastrar um produto |
| PUT | `/products/{id}` | Atualizar um produto |
| DELETE | `/products/{id}` | Desativar um produto |
| GET | `/orders` | Listar pedidos |
| GET | `/orders/{id}` | Consultar um pedido |
| POST | `/orders` | Criar um pedido |

Consulte a documentação da API para obter detalhes sobre requisições e respostas.

## 10. Documentação

A documentação técnica está organizada em:

```text
docs/
```

Documentos disponíveis:

| Documento | Descrição |
|---|---|
| `arquitetura.md` | Arquitetura do sistema |
| `api.md` | Documentação da API REST |
| `banco-de-dados.md` | Documentação do banco de dados |
| `config-ambiente-desenvolvimento.md` | Configuração do ambiente |
| `testes.md` | Estratégia de testes |

As fases anteriores do projeto e os diagramas UML estão organizados nos respectivos diretórios de documentação.

## 11. Limitações atuais

A implementação atual não contempla:

- Autenticação ou autorização de usuários.
- Integração com serviços de pagamento.
- Upload de imagens.
- Carrinho de compras persistente.
- Gerenciamento de status dos pedidos.
- Cancelamento de pedidos.
- Implementação do frontend.
- Verificação da titularidade dos pedidos.

A API não deve ser disponibilizada publicamente sem tratar as limitações de segurança existentes, especialmente o acesso a informações dos clientes pelos endpoints de consulta de pedidos.

## 12. Projeto acadêmico

Esta aplicação é um projeto acadêmico desenvolvido para aplicar conceitos de Engenharia de Software, incluindo análise de requisitos, modelagem UML, arquitetura de software, projeto de banco de dados, implementação e testes automatizados.


## 👤 Author

**Lussac Prestes Maia**

- GitHub: [@lussac7](https://github.com/lussac7/task-manager)
- LinkedIn: [Lussac P. Maia](https://linkedin.com/in/lussac-maia-eng-software)
