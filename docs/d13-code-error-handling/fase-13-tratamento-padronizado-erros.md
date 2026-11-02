# Cupcake-Unicsul --- Fase 13: Tratamento Padronizado de Erros

**Status:** Concluída\
**Verificação final:** `mvn clean test` --- `BUILD SUCCESS`

## 1. Objetivo

Padronizar as respostas de erro da API REST utilizando `ProblemDetail`,
conforme o formato Problem Details for HTTP APIs (RFC 9457),
centralizando o tratamento de exceções em um componente global.

## 2. Implementação

### 2.1 Exceções específicas

Pacote: `shared.exception`

-   `ResourceNotFoundException`: representa a tentativa de acessar um
    recurso inexistente ou não disponível.
-   `BusinessRuleException`: representa uma violação de regra de
    negócio.

Os serviços `ProductService` e `OrderService` utilizam essas exceções
nos casos apropriados.

### 2.2 Tratamento global

Arquivo:

`src/main/java/io/github/lussac7/cupcake/shared/exception/GlobalExceptionHandler.java`

A classe é anotada com `@RestControllerAdvice` e converte exceções em
respostas `ProblemDetail`.

  Situação                       Status HTTP Tratamento
  ---------------------------- ------------- -----------------------------------
  Recurso não encontrado                 404 `ResourceNotFoundException`
  Regra de negócio violada               409 `BusinessRuleException`
  Erros de validação                     400 `MethodArgumentNotValidException`
  JSON ausente ou malformado             400 `HttpMessageNotReadableException`
  Erro inesperado                        500 `Exception`

As respostas de validação incluem a propriedade adicional `errors`, com
o nome do campo e a mensagem correspondente.

Para erros inesperados, a resposta apresenta uma mensagem genérica. Os
detalhes da exceção são registrados no log do servidor, sem serem
expostos ao cliente.

Não foram incluídos tratamentos 401 ou 403, pois o escopo atual da
aplicação não possui autenticação ou autorização.

## 3. Testes

Arquivo:

`src/test/java/io/github/lussac7/cupcake/shared/exception/GlobalExceptionHandlerTest.java`

Os testes de integração MVC verificam:

-   resposta 404 para recurso inexistente;
-   resposta 409 para violação de regra de negócio;
-   resposta 400 para falha de validação;
-   resposta 400 para JSON malformado;
-   resposta 500 para erro inesperado, sem exposição de detalhes
    internos.

### Isolamento do contexto MVC

A configuração de teste utiliza:

``` java
@WebMvcTest(useDefaultFilters = false)
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
```

Isso evita o carregamento automático dos controllers reais da aplicação
e registra explicitamente apenas o controller auxiliar e o handler
necessários ao teste.

## 4. Revisão dos serviços

Foram revisados `ProductService` e `OrderService`.

Conclusões:

-   Não foram identificadas exceções genéricas que precisassem ser
    substituídas nesta fase.
-   Recursos inexistentes são tratados com `ResourceNotFoundException`.
-   Produtos inativos não podem ser incluídos em pedidos e geram
    `BusinessRuleException`.
-   Produto inativo consultado pelo endpoint de produto ativo retorna
    404, comportamento mantido intencionalmente.

Nenhuma alteração adicional nos dois serviços foi necessária.

## 5. Resultado por incremento

  --------------------------------------------------------------------------
  Incremento              Descrição                  Resultado
  ----------------------- -------------------------- -----------------------
  13.1                    Criação das exceções       Concluído
                          específicas                

  13.2                    Implementação do           Concluído
                          `GlobalExceptionHandler`   

  13.3                    Testes do tratamento       Concluído
                          global                     

  13.4                    Revisão das exceções nos   Concluído --- sem
                          serviços                   alterações necessárias
  --------------------------------------------------------------------------

## 6. Verificação final

Comando executado:

``` bash
mvn clean test
```

**Resultado informado: `BUILD SUCCESS`.**

A Fase 13 está concluída. O tratamento de erros da API está
centralizado, padronizado e coberto por testes automatizados.

## 7. Escopo e observações

Esta fase não introduziu autenticação, autorização, novos endpoints ou
alterações na arquitetura da aplicação. O escopo foi restrito ao
tratamento padronizado de erros e à revisão das exceções utilizadas
pelos serviços.
