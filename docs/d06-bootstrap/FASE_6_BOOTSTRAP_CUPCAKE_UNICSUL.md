# Fase 6 — Bootstrap do Projeto

**Projeto:** `cupcake-unicsul`  
**Group ID:** `io.github.lussac7`  
**Artifact ID:** `cupcake`  
**Java:** 21  
**Spring Boot:** 4.1.2  
**Build:** Maven  
**Status:** arquivos iniciais preparados para instalação no projeto local.

## 1. Arquivos criados

- `pom.xml`: configuração Maven, dependências web MVC e testes, plugin de empacotamento Spring Boot.
- `src/main/java/io/github/lussac7/cupcake/CupcakeApplication.java`: classe principal da aplicação.
- `src/main/resources/application.yml`: configuração mínima com o nome da aplicação.

## 2. Dependências

| Dependência | Escopo | Finalidade |
|---|---|---|
| `spring-boot-starter-webmvc` | compile | Aplicação web Spring MVC e API REST. |
| `spring-boot-starter-test` | test | Infraestrutura de testes, incluindo JUnit e Mockito. |

PostgreSQL, Spring Data JPA, Flyway e validação ainda não foram adicionados. Serão incluídos nas fases de configuração e migração, conforme o plano do projeto.

## 3. Versões e compatibilidade

Foi adotado Spring Boot 4.1.2, versão estável indicada pela documentação oficial consultada nesta fase. O projeto compila para Java 21. A documentação oficial informa suporte do Spring Boot 4.1.2 a Java 17 ou superior e Maven 3.6.3 ou superior.

## 4. Instalação

Extraia o arquivo ZIP na raiz `/projects/java/cupcake-unicsul`, preservando os caminhos. Os arquivos `pom.xml`, `CupcakeApplication.java` e `application.yml` serão adicionados.

Se já existir algum desses arquivos, faça backup antes de extrair e revise as diferenças. Não substitua alterações locais inadvertidamente.

## 5. Verificação local

Na raiz do projeto:

```bash
java -version
mvn -version
mvn clean test
mvn spring-boot:run
```

A aplicação deverá iniciar o servidor web embutido. Como ainda não existem controllers, não há endpoints de negócio implementados nesta fase.

Para encerrar, pressione `Ctrl+C`.

Também é possível empacotar:

```bash
mvn clean package
java -jar target/cupcake-0.0.1-SNAPSHOT.jar
```

## 6. Limites desta fase

Ainda não foram implementados:

- conexão PostgreSQL;
- entidades JPA e repositories;
- migrações Flyway;
- controllers de negócio;
- DTOs e validações;
- perfis `dev` e `test`;
- configuração de credenciais e variáveis de ambiente.

Esses itens pertencem às fases seguintes.

## 7. Critério de conclusão

A Fase 6 estará concluída quando o usuário aplicar os arquivos ao projeto local e confirmar que:

1. `mvn clean test` termina com sucesso;
2. `mvn spring-boot:run` inicia a aplicação sem erros.

**Status:** aguardando verificação local. Após a confirmação, avançaremos à Fase 7 — Configuração.
