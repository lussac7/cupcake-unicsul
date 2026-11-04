```texto
/projects/java/cupcake-unicsul
├── docs
│   ├── api.md
│   ├── arquitetura.md
│   ├── banco-de-dados.md
│   ├── config-ambiente-desenvolvimento.md
│   ├── testes.md
│   └── uml
│       ├── 02-casos-de-uso.png
│       ├── 03-arquitetura-camadas.png
│       ├── 04a-classes-dominio.png
│       ├── 04-modelo-dominio.png
│       └── 05-modelo-ER.png
├── estrutura-app-cupcake-unicsul.md
├── frontend
│   ├── assets
│   │   └── images
│   ├── css
│   │   ├── components.css
│   │   └── styles.css
│   ├── index.html
│   └── js
│       ├── api
│       │   ├── http-client.js
│       │   ├── order-api.js
│       │   └── product-api.js
│       ├── app.js
│       ├── components
│       │   ├── cart-summary.js
│       │   ├── footer.js
│       │   ├── header.js
│       │   ├── pagination.js
│       │   └── product-card.js
│       ├── pages
│       │   ├── cart-page.js
│       │   ├── catalog-page.js
│       │   ├── checkout-page.js
│       │   ├── order-page.js
│       │   └── product-detail-page.js
│       ├── services
│       │   └── cart-service.js
│       └── utils
│           ├── formatters.js
│           └── validators.js
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── io
    │   │       └── github
    │   │           └── lussac7
    │   │               └── cupcake
    │   │                   ├── CupcakeApplication.java
    │   │                   ├── order
    │   │                   │   ├── controller
    │   │                   │   │   └── OrderController.java
    │   │                   │   ├── dto
    │   │                   │   │   ├── OrderCreateRequest.java
    │   │                   │   │   ├── OrderItemRequest.java
    │   │                   │   │   ├── OrderItemResponse.java
    │   │                   │   │   └── OrderResponse.java
    │   │                   │   ├── entity
    │   │                   │   │   ├── OrderItem.java
    │   │                   │   │   └── Order.java
    │   │                   │   ├── repository
    │   │                   │   │   └── OrderRepository.java
    │   │                   │   └── service
    │   │                   │       └── OrderService.java
    │   │                   ├── product
    │   │                   │   ├── controller
    │   │                   │   │   └── ProductController.java
    │   │                   │   ├── dto
    │   │                   │   │   ├── ProductCreateRequest.java
    │   │                   │   │   ├── ProductResponse.java
    │   │                   │   │   └── ProductUpdateRequest.java
    │   │                   │   ├── entity
    │   │                   │   │   └── Product.java
    │   │                   │   ├── repository
    │   │                   │   │   └── ProductRepository.java
    │   │                   │   └── service
    │   │                   │       └── ProductService.java
    │   │                   └── shared
    │   │                       ├── exception
    │   │                       │   ├── BusinessRuleException.java
    │   │                       │   ├── GlobalExceptionHandler.java
    │   │                       │   └── ResourceNotFoundException.java
    │   │                       └── web
    │   └── resources
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── application.yml
    │       └── db
    │           └── migration
    │               └── V1__initial_schema.sql
    └── test
        ├── java
        │   └── io
        │       └── github
        │           └── lussac7
        │               └── cupcake
        │                   ├── dto
        │                   │   └── DTOValidationTest.java
        │                   ├── integration
        │                   │   └── PostgreSqlIntegrationTest.java
        │                   ├── order
        │                   │   ├── controller
        │                   │   │   └── OrderControllerTest.java
        │                   │   ├── dto
        │                   │   ├── entity
        │                   │   │   └── OrderTest.java
        │                   │   ├── repository
        │                   │   │   └── OrderRepositoryTest.java
        │                   │   └── service
        │                   │       └── OrderServiceTest.java
        │                   ├── product
        │                   │   ├── controller
        │                   │   │   └── ProductControllerTest.java
        │                   │   ├── dto
        │                   │   ├── entity
        │                   │   │   └── ProductTest.java
        │                   │   ├── repository
        │                   │   │   └── ProductRepositoryTest.java
        │                   │   └── service
        │                   │       └── ProductServiceTest.java
        │                   └── shared
        │                       ├── exception
        │                       │   └── GlobalExceptionHandlerTest.java
        │                       └── web
        └── resources
            └── application-test.yml

62 directories, 69 files
```
