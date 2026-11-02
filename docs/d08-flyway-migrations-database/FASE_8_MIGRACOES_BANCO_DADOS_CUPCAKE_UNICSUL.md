# Fase 8 — Migrações do Banco de Dados

**Projeto:** Cupcake Unicsul  
**Módulo:** Backend  
**Status:** Concluída  
**Banco de dados:** PostgreSQL 18.6  
**Ferramenta de migração:** Flyway

## 1. Objetivo

Criar o esquema inicial do banco de dados PostgreSQL por meio de uma migration versionada do Flyway e validar sua execução durante a inicialização da aplicação Spring Boot.

## 2. Migration criada

Arquivo:

```text
src/main/resources/db/migration/V1__initial_schema.sql
```

A migration cria três tabelas da aplicação:

- `products`
- `orders`
- `order_items`

Também cria as constraints, chaves estrangeiras e índices definidos na Fase 5 — Projeto do Banco de Dados.

O Flyway cria adicionalmente a tabela de controle `flyway_schema_history`.

## 3. Configuração do banco

| Propriedade | Valor |
|---|---|
| Banco de dados | `cupcake` |
| Usuário da aplicação | `cupcake_user` |
| Proprietário do banco | `cupcake_user` |
| Host | `localhost` |
| Porta | `5432` |
| Perfil Spring ativo | `dev` |

A senha é fornecida por variável de ambiente e não deve ser armazenada no repositório.

Variáveis utilizadas:

```text
DB_URL=jdbc:postgresql://localhost:5432/cupcake
DB_USERNAME=cupcake_user
DB_PASSWORD=<senha definida localmente>
SPRING_PROFILES_ACTIVE=dev
```

## 4. Inicialização da aplicação

A aplicação foi iniciada com:

```bash
mvn spring-boot:run
```

O log confirmou a inicialização:

```text
Started CupcakeApplication in 3.583 seconds
(process running for 3.872)
```

Durante a inicialização, o Spring Boot conectou-se ao PostgreSQL e executou a migration pelo Flyway.

## 5. Correção realizada durante a execução

Na primeira tentativa, o Flyway encontrou duas migrations com a mesma versão:

```text
V1__initial_schema.sql
V1__create_initial_schema.sql
```

O problema foi resolvido mantendo somente `V1__initial_schema.sql`. Em seguida, foi executado `mvn clean` para remover os recursos compilados antigos, e a aplicação foi iniciada novamente com sucesso.

## 6. Validação no PostgreSQL

A conexão foi testada com:

```bash
psql -h localhost -U cupcake_user -d cupcake -W
```

O comando `\dt` apresentou as seguintes tabelas:

```text
flyway_schema_history
order_items
orders
products
```

O histórico do Flyway foi consultado com:

```sql
SELECT installed_rank,
       version,
       description,
       success
FROM flyway_schema_history;
```

Resultado confirmado:

| installed_rank | version | description | success |
|---:|---:|---|---|
| 1 | 1 | initial schema | true |

## 7. Critérios de conclusão

| Critério | Resultado |
|---|---|
| PostgreSQL instalado e ativo | Confirmado |
| Usuário `cupcake_user` criado | Confirmado |
| Banco `cupcake` criado | Confirmado |
| Conexão TCP com o banco | Confirmada |
| Perfil `dev` ativado | Confirmado no log |
| Aplicação Spring Boot inicializada | Confirmado |
| Migration V1 executada | Confirmado |
| Tabelas `products`, `orders` e `order_items` criadas | Confirmado |
| Histórico do Flyway registra V1 como sucesso | Confirmado |

## 8. Estado final

A Fase 8 está concluída. O projeto possui um banco PostgreSQL preparado e um esquema inicial gerenciado pelo Flyway.

A migration V1 deve ser mantida no controle de versão. Depois de aplicada em um ambiente compartilhado ou de produção, migrations versionadas não devem ser editadas retroativamente; alterações futuras devem ser introduzidas em novas versões, como `V2__...sql`.

## 9. Próxima etapa

Avançar para a próxima fase do plano do projeto, consultando o `MASTER_PROMPT_CUPCAKE_UNICSUL.md` para identificar a sequência e os critérios correspondentes.
