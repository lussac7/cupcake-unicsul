# Documentação da API REST

## 1. Visão geral

A aplicação Cupcake disponibiliza uma API REST para gerenciamento de produtos e pedidos.

A API foi desenvolvida com Java 21 e Spring Boot 4.1.2, utilizando Spring Web MVC.

### Endereço-base

```text
/api/v1
```

Os endpoints estão organizados em dois recursos principais:

- `/products` — gerenciamento de produtos.
- `/orders` — gerenciamento de pedidos.

As entidades JPA não são expostas diretamente. A comunicação utiliza DTOs para entrada e saída de dados.

## 2. Produtos

Recurso responsável pelo gerenciamento dos produtos da loja.

### 2.1 Listar produtos ativos

```http
GET /api/v1/products
```

Retorna uma lista paginada de produtos ativos.

Parâmetros de consulta:

| Parâmetro | Descrição | Padrão |
|---|---|---|
| `page` | Número da página | 0 |
| `size` | Quantidade de registros por página | 20 |
| `sort` | Campo utilizado na ordenação | price |
| `direction` | Direção da ordenação | ASC |

Exemplo:

```http
GET /api/v1/products?page=0&size=10&sort=price&direction=ASC
```

A ordenação padrão é pelo preço em ordem crescente.

**Resposta:** `200 OK`

O corpo da resposta contém uma página de objetos `ProductResponse`.

### 2.2 Consultar produto por ID

```http
GET /api/v1/products/{id}
```

Retorna os dados de um produto ativo.

Exemplo:

```http
GET /api/v1/products/1
```

**Resposta:** `200 OK`

Se o produto não existir ou estiver inativo:

**Resposta:** `404 Not Found`

### 2.3 Criar produto

```http
POST /api/v1/products
```

Cria um novo produto.

Exemplo de requisição:

```json
{
  "name": "Cupcake de Chocolate",
  "description": "Cupcake de chocolate com cobertura cremosa.",
  "price": 12.50,
  "imageUrl": "https://example.com/cupcake-chocolate.jpg"
}
```

Campos:

| Campo | Tipo | Obrigatório | Restrições |
|---|---|---|---|
| `name` | String | Sim | Não vazio; máximo de 150 caracteres |
| `description` | String | Sim | Não vazio |
| `price` | Decimal | Sim | Mínimo 0,01; até 8 dígitos inteiros e 2 decimais |
| `imageUrl` | String | Não | Máximo de 500 caracteres |

**Resposta:** `201 Created`

A resposta contém um objeto `ProductResponse`.

O cabeçalho `Location` informa o endereço do recurso criado:

```http
Location: /api/v1/products/{id}
```

### 2.4 Atualizar produto

```http
PUT /api/v1/products/{id}
```

Atualiza as informações de um produto existente.

Exemplo:

```http
PUT /api/v1/products/1
```

Corpo da requisição:

```json
{
  "name": "Cupcake de Chocolate Especial",
  "description": "Cupcake de chocolate com cobertura especial.",
  "price": 14.00,
  "imageUrl": "https://example.com/cupcake-especial.jpg"
}
```

Os campos seguem as mesmas regras de validação da criação.

**Resposta:** `200 OK`

Se o produto não existir:

**Resposta:** `404 Not Found`

### 2.5 Desativar produto

```http
DELETE /api/v1/products/{id}
```

Desativa um produto.

A operação não representa a exclusão física do registro no banco de dados.

**Resposta:** `204 No Content`

Um produto desativado deixa de aparecer nas consultas de produtos ativos e não pode ser incluído em novos pedidos.

**Observação:** não existe atualmente um endpoint REST para reativar produtos.

## 3. Pedidos

Recurso responsável pelo gerenciamento dos pedidos dos clientes.

### 3.1 Criar pedido

```http
POST /api/v1/orders
```

Cria um novo pedido.

Exemplo:

```json
{
  "customerName": "João da Silva",
  "customerEmail": "joao@example.com",
  "deliveryAddress": "Rua das Flores, 100",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

Campos da requisição:

| Campo | Tipo | Obrigatório | Restrições |
|---|---|---|---|
| `customerName` | String | Sim | Não vazio; máximo de 150 caracteres |
| `customerEmail` | String | Sim | E-mail válido; máximo de 254 caracteres |
| `deliveryAddress` | String | Sim | Não vazio |
| `items` | Lista | Sim | Deve conter pelo menos um item |

Campos de cada item:

| Campo | Tipo | Obrigatório | Restrições |
|---|---|---|---|
| `productId` | Long | Sim | Valor positivo |
| `quantity` | Integer | Sim | Valor positivo |

O cliente informa apenas os identificadores dos produtos e as respectivas quantidades.

O backend consulta os preços atuais no banco de dados e calcula os subtotais e o valor total do pedido.

O preço informado ou calculado pelo frontend não é utilizado como fonte de verdade.

Quando um mesmo produto aparece mais de uma vez na requisição, suas quantidades são consolidadas pelo serviço.

Produtos inexistentes ou inativos não podem ser incluídos no pedido.

O pedido armazena uma cópia do nome e do preço unitário dos produtos no momento da compra. Dessa forma, alterações posteriores no cadastro dos produtos não modificam o histórico do pedido.

**Resposta:** `201 Created`

O corpo contém um objeto `OrderResponse`.

O cabeçalho `Location` informa o endereço do pedido criado:

```http
Location: /api/v1/orders/{id}
```

### 3.2 Consultar pedido por ID

```http
GET /api/v1/orders/{id}
```

Retorna os dados de um pedido específico.

**Resposta:** `200 OK`

Se o pedido não existir:

**Resposta:** `404 Not Found`

O objeto de resposta contém os dados do cliente, a data de criação, o valor total e os itens do pedido.

### 3.3 Listar pedidos

```http
GET /api/v1/orders
```

Retorna uma lista paginada de pedidos.

Parâmetros de consulta:

| Parâmetro | Descrição | Padrão |
|---|---|---|
| `page` | Número da página | 0 |
| `size` | Quantidade de registros por página | 20 |
| `sort` | Campo utilizado na ordenação | createdAt |
| `direction` | Direção da ordenação | DESC |

Exemplo:

```http
GET /api/v1/orders?page=0&size=10&sort=createdAt&direction=DESC
```

A ordenação padrão é pela data de criação, da mais recente para a mais antiga.

**Resposta:** `200 OK`

O corpo contém uma página de objetos `OrderResponse`.

## 4. Estrutura dos DTOs de resposta

### 4.1 ProductResponse

Representa um produto retornado pela API.

```json
{
  "id": 1,
  "name": "Cupcake de Chocolate",
  "description": "Cupcake de chocolate com cobertura cremosa.",
  "price": 12.50,
  "imageUrl": "https://example.com/cupcake-chocolate.jpg",
  "active": true
}
```

### 4.2 OrderResponse

Representa um pedido retornado pela API.

```json
{
  "id": 1,
  "customerName": "João da Silva",
  "customerEmail": "joao@example.com",
  "deliveryAddress": "Rua das Flores, 100",
  "createdAt": "2026-10-31T15:30:00Z",
  "total": 37.50,
  "items": [
    {
      "productId": 1,
      "productName": "Cupcake de Chocolate",
      "unitPrice": 12.50,
      "quantity": 2,
      "subtotal": 25.00
    },
    {
      "productId": 2,
      "productName": "Cupcake de Baunilha",
      "unitPrice": 12.50,
      "quantity": 1,
      "subtotal": 12.50
    }
  ]
}
```

Os valores apresentados são ilustrativos.

### 4.3 OrderItemResponse

Cada item de pedido contém:

| Campo | Descrição |
|---|---|
| `productId` | Identificador do produto |
| `productName` | Nome do produto no momento da compra |
| `unitPrice` | Preço unitário no momento da compra |
| `quantity` | Quantidade solicitada |
| `subtotal` | Resultado de preço unitário multiplicado pela quantidade |

## 5. Tratamento de erros HTTP

A API utiliza um tratamento centralizado de exceções por meio de `GlobalExceptionHandler`.

As respostas de erro utilizam o formato `ProblemDetail`.

Principais situações:

| Status HTTP | Situação |
|---|---|
| `400 Bad Request` | Dados inválidos ou requisição malformada |
| `404 Not Found` | Recurso não encontrado |
| `409 Conflict` | Violação de uma regra de negócio |
| `500 Internal Server Error` | Erro inesperado no servidor |

### 5.1 Erro de validação

O status `400 Bad Request` é utilizado quando os dados recebidos não atendem às validações definidas nos DTOs ou quando a requisição não pode ser interpretada corretamente.

### 5.2 Recurso não encontrado

O status `404 Not Found` é utilizado quando o recurso solicitado não existe ou não está disponível para consulta.

### 5.3 Regra de negócio violada

O status `409 Conflict` é utilizado quando uma operação viola uma regra de negócio da aplicação.

Exemplos incluem tentativas de realizar operações incompatíveis com o estado atual dos recursos.

### 5.4 Erro inesperado

O status `500 Internal Server Error` representa uma falha inesperada durante o processamento da requisição.

## 6. Considerações de segurança

A versão atual da API não implementa autenticação nem autorização.

Consequentemente:

- Não há identificação autenticada do cliente.
- Não há controle de permissões por perfil.
- Os endpoints de pedidos não verificam a titularidade do pedido.
- As consultas de pedidos podem retornar dados pessoais, como e-mail e endereço de entrega.

Essas limitações precisam ser consideradas antes de disponibilizar a API publicamente.

## 7. Integração com o frontend

O projeto possui um frontend independente, desenvolvido com HTML5, CSS3 e JavaScript puro (Vanilla JavaScript), organizado em módulos ES.

A comunicação entre frontend e backend ocorre por meio da API REST, utilizando a Fetch API do JavaScript.

### 7.1 Funcionalidades integradas

O frontend disponibiliza as seguintes funcionalidades:

- Consulta e exibição do catálogo de produtos.
- Consulta dos detalhes de um produto.
- Gerenciamento do carrinho de compras.
- Atualização das quantidades e remoção de itens.
- Checkout e criação de pedidos.
- Consulta de pedidos por identificador.

### 7.2 Navegação

A navegação utiliza rotas Hash no navegador:

| Rota | Funcionalidade |
|---|---|
| `#/products` | Catálogo de produtos |
| `#/products/{id}` | Detalhes do produto |
| `#/cart` | Carrinho de compras |
| `#/checkout` | Finalização do pedido |
| `#/orders/{id}` | Consulta do pedido |

### 7.3 Carrinho de compras

Os dados do carrinho são armazenados localmente no navegador por meio de `localStorage`, utilizando a chave `cupcake.cart`.

O carrinho mantém os identificadores dos produtos e suas quantidades. Os preços e valores totais não são considerados confiáveis quando armazenados no navegador.

### 7.4 Responsabilidade pelo cálculo dos valores

O frontend envia ao backend os identificadores dos produtos e suas respectivas quantidades durante a criação do pedido.

O backend permanece como fonte da verdade para os valores financeiros, consultando os preços armazenados no banco de dados e calculando os subtotais e o total do pedido.

Produtos inexistentes ou inativos não podem ser incluídos em novos pedidos.

### 7.5 Configuração de comunicação

O backend disponibiliza a API na porta `8080`.

O frontend é uma aplicação estática independente, servida por um servidor HTTP na porta `5500`.

A configuração CORS do backend permite requisições originadas de:

```text
http://localhost:5500
```

## 8. Funcionalidades não disponíveis

A API atual não oferece:

- Autenticação de usuários.
- Autorização ou gerenciamento de perfis.
- Integração com serviços de pagamento.
- Upload de imagens.
- Reativação de produtos por endpoint REST.
- Atualização ou cancelamento de pedidos.
- Gerenciamento de status dos pedidos.
- Consulta de pedidos com verificação de titularidade.

## 9. Considerações finais

A API REST implementa as operações essenciais de consulta e gerenciamento de produtos e pedidos.

A separação entre controllers, serviços, repositórios, entidades e DTOs permite manter os contratos HTTP independentes do modelo de persistência.

A documentação deverá ser atualizada sempre que novos endpoints, regras de negócio ou alterações nos contratos da API forem implementados.
