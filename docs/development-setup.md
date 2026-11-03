# Configuração do Ambiente de Desenvolvimento

## 1. Visão geral

Este documento apresenta os procedimentos necessários para preparar o ambiente de desenvolvimento e executar localmente o backend da aplicação Cupcake.

O projeto utiliza Java 21, Spring Boot 4.1.2, Maven e PostgreSQL.

Os testes automatizados utilizam H2 e Testcontainers, conforme o tipo de teste executado.

## 2. Pré-requisitos

Antes de executar a aplicação, verifique se os seguintes componentes estão disponíveis:

| Componente | Versão ou requisito |
|---|---|
| Java Development Kit | Java 21 |
| Maven | Compatível com o projeto |
| PostgreSQL | Instalado e em execução |
| Git | Instalado |
| Docker | Necessário para os testes de integração com Testcontainers |
| IDE | Opcional |

O projeto pode ser desenvolvido utilizando uma IDE ou diretamente pelo terminal.

## 3. Obter o código-fonte

Clone o repositório Git do projeto:

```bash
git clone <repository-url>
```

Entre no diretório:

```bash
cd cupcake-unicsul
```

Verifique o conteúdo:

```bash
ls
```

A estrutura principal deve conter:

```text
cupcake-unicsul/
├── pom.xml
├── src/
├── docs/
├── README.md
└── LEIAME.md
```

## 4. Verificar Java e Maven

Verifique a versão do Java:

```bash
java -version
```

O ambiente deve utilizar Java 21.

Verifique a versão do compilador:

```bash
javac -version
```

Verifique a instalação do Maven:

```bash
mvn -version
```

Confirme que o Maven está utilizando Java 21.

## 5. Configurar PostgreSQL

O perfil de desenvolvimento utiliza PostgreSQL.

A configuração padrão da aplicação é:

```text
jdbc:postgresql://localhost:5432/cupcake
```

A aplicação espera encontrar um banco de dados chamado `cupcake`.

### 5.1 Criar banco de dados e usuário

Se ainda não existirem, crie um usuário e o banco de dados para desenvolvimento.

Exemplo de comandos SQL:

```sql
CREATE USER cupcake_user WITH PASSWORD 'change_this_password';

CREATE DATABASE cupcake
    OWNER cupcake_user;
```

Os nomes e as credenciais acima são exemplos. Utilize valores apropriados para seu ambiente local.

### 5.2 Verificar conexão

Verifique se o PostgreSQL está em execução e se o banco pode ser acessado.

Exemplo:

```bash
psql -h localhost -U cupcake_user -d cupcake
```

A senha será solicitada pelo cliente PostgreSQL.

## 6. Configurar variáveis de ambiente

O perfil `dev` utiliza variáveis de ambiente para obter as credenciais do banco de dados.

Variáveis necessárias:

| Variável | Descrição |
|---|---|
| `DB_USERNAME` | Usuário do PostgreSQL |
| `DB_PASSWORD` | Senha do PostgreSQL |

Exemplo para uma sessão do terminal:

```bash
export DB_USERNAME=cupcake_user
export DB_PASSWORD='change_this_password'
```

Verifique se as variáveis foram configuradas:

```bash
printenv DB_USERNAME
```

Não armazene credenciais reais no código-fonte nem as envie ao repositório Git.

## 7. Configurações do Spring Boot

O projeto possui configurações específicas por ambiente.

### 7.1 application.yml

Arquivo:

```text
src/main/resources/application.yml
```

Contém as configurações gerais da aplicação, incluindo:

- Nome da aplicação.
- Configuração do Hibernate.
- Configuração do Flyway.
- Logging.
- Desativação do Open Session in View.

O Hibernate utiliza:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

O Flyway está habilitado para gerenciar as migrações do banco de dados.

### 7.2 application-dev.yml

Arquivo:

```text
src/main/resources/application-dev.yml
```

Utilizado no desenvolvimento local.

Configura:

- PostgreSQL.
- Endereço de conexão.
- Credenciais por variáveis de ambiente.
- Logging DEBUG para os pacotes da aplicação.

### 7.3 application-test.yml

Arquivo:

```text
src/test/resources/application-test.yml
```

Utilizado durante os testes automatizados.

Configura:

- Banco H2 em memória.
- Modo de compatibilidade PostgreSQL.
- Hibernate com `create-drop`.
- Flyway desabilitado.

## 8. Executar a aplicação

Depois de configurar o PostgreSQL e as variáveis de ambiente, execute:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O Spring Boot iniciará a aplicação utilizando o perfil `dev`.

Na inicialização, o Flyway verificará e aplicará as migrações pendentes.

O Hibernate validará a compatibilidade entre as entidades JPA e o esquema do banco de dados.

Verifique os logs para confirmar que a aplicação iniciou corretamente.

## 9. Compilar o projeto

Para compilar o código-fonte:

```bash
mvn clean compile
```

Esse comando remove os arquivos de compilação anteriores e compila o projeto.

## 10. Executar os testes

Para executar todos os testes automatizados:

```bash
mvn clean test
```

Os testes utilizam as configurações apropriadas para cada tipo de teste.

### 10.1 Testes com H2

Os testes que utilizam o perfil `test` são executados com banco H2 em memória.

### 10.2 Testes de integração com PostgreSQL

Os testes de integração que utilizam Testcontainers precisam de um ambiente Docker funcional.

O Testcontainers inicia automaticamente uma instância PostgreSQL utilizando a imagem:

```text
postgres:16-alpine
```

Não é necessário configurar manualmente um banco PostgreSQL adicional para esses testes.

## 11. Empacotar a aplicação

Para gerar o artefato executável:

```bash
mvn clean package
```

O Maven executará as etapas de compilação, testes e empacotamento.

O artefato gerado ficará no diretório:

```text
target/
```

## 12. Executar o JAR

Depois de gerar o artefato, a aplicação pode ser iniciada com:

```bash
java -jar target/cupcake-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=dev
```

As variáveis `DB_USERNAME` e `DB_PASSWORD` precisam estar disponíveis no ambiente de execução.

## 13. Encerrar a aplicação

Se a aplicação estiver sendo executada no terminal, utilize:

```text
Ctrl + C
```

O processo será encerrado.

## 14. Problemas comuns

### 14.1 Falha na conexão com PostgreSQL

Verifique:

- Se o serviço PostgreSQL está em execução.
- Se o banco `cupcake` existe.
- Se o endereço e a porta estão corretos.
- Se `DB_USERNAME` e `DB_PASSWORD` estão configuradas.
- Se o usuário possui permissões adequadas.

### 14.2 Falha na validação do esquema

Se o Hibernate informar incompatibilidade entre as entidades e o banco de dados, verifique:

- As entidades JPA.
- A migração Flyway correspondente.
- O histórico de migrações aplicadas.
- A estrutura efetiva das tabelas.

Não altere `ddl-auto: validate` para `update` como solução automática. O esquema deve continuar sendo controlado pelo Flyway.

### 14.3 Falha nos testes de integração

Se os testes com Testcontainers falharem, verifique:

- Se o Docker está instalado.
- Se o serviço Docker está em execução.
- Se o usuário possui permissão para utilizar o Docker.
- Se a imagem PostgreSQL necessária pode ser obtida.
- Se existem recursos suficientes para executar o container.

### 14.4 Variáveis de ambiente não encontradas

Verifique se as variáveis estão disponíveis na mesma sessão do terminal utilizada para iniciar a aplicação:

```bash
printenv DB_USERNAME
printenv DB_PASSWORD
```

## 15. Considerações finais

O ambiente de desenvolvimento utiliza PostgreSQL para executar a aplicação e H2 para os testes que utilizam o perfil `test`.

Os testes de integração utilizam PostgreSQL por meio de Testcontainers, permitindo validar o comportamento da persistência em um banco compatível com o ambiente principal.

As credenciais devem permanecer fora do código-fonte.

A configuração do projeto deve continuar centralizada nos arquivos de configuração do Spring Boot e nas variáveis de ambiente.
