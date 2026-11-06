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
│       ├── 05-modelo-ER.png
│       └── 17-frontend-arquitetura.png
├── estrutura-app-cupcake-unicsul.md
├── frontend
│   ├── assets
│   │   └── images
│   │       ├── cupcake-placeholder.svg
│   │       └── favicon.svg
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
├── mvnw
├── mvnw.cmd
├── pom.xml
├── pom.xml_backup_2026-11-05T22:08:19-03:00
├── README.md
├── README.pdf
├── run-backend.sh
├── run-frontend.sh
├── src
│   ├── main
│   │   ├── java
│   │   │   └── io
│   │   │       └── github
│   │   │           └── lussac7
│   │   │               └── cupcake
│   │   │                   ├── CupcakeApplication.java
│   │   │                   ├── order
│   │   │                   │   ├── controller
│   │   │                   │   │   └── OrderController.java
│   │   │                   │   ├── dto
│   │   │                   │   │   ├── OrderCreateRequest.java
│   │   │                   │   │   ├── OrderItemRequest.java
│   │   │                   │   │   ├── OrderItemResponse.java
│   │   │                   │   │   └── OrderResponse.java
│   │   │                   │   ├── entity
│   │   │                   │   │   ├── OrderItem.java
│   │   │                   │   │   └── Order.java
│   │   │                   │   ├── repository
│   │   │                   │   │   └── OrderRepository.java
│   │   │                   │   └── service
│   │   │                   │       └── OrderService.java
│   │   │                   ├── product
│   │   │                   │   ├── controller
│   │   │                   │   │   └── ProductController.java
│   │   │                   │   ├── dto
│   │   │                   │   │   ├── ProductCreateRequest.java
│   │   │                   │   │   ├── ProductResponse.java
│   │   │                   │   │   └── ProductUpdateRequest.java
│   │   │                   │   ├── entity
│   │   │                   │   │   └── Product.java
│   │   │                   │   ├── repository
│   │   │                   │   │   └── ProductRepository.java
│   │   │                   │   └── service
│   │   │                   │       └── ProductService.java
│   │   │                   └── shared
│   │   │                       ├── config
│   │   │                       │   └── CorsConfig.java
│   │   │                       ├── exception
│   │   │                       │   ├── BusinessRuleException.java
│   │   │                       │   ├── GlobalExceptionHandler.java
│   │   │                       │   └── ResourceNotFoundException.java
│   │   │                       └── web
│   │   └── resources
│   │       ├── application-dev.yml
│   │       ├── application.yml
│   │       └── db
│   │           └── migration
│   │               └── V1__initial_schema.sql
│   └── test
│       ├── java
│       │   └── io
│       │       └── github
│       │           └── lussac7
│       │               └── cupcake
│       │                   ├── dto
│       │                   │   └── DTOValidationTest.java
│       │                   ├── integration
│       │                   │   └── PostgreSqlIntegrationTest.java
│       │                   ├── order
│       │                   │   ├── controller
│       │                   │   │   └── OrderControllerTest.java
│       │                   │   ├── dto
│       │                   │   ├── entity
│       │                   │   │   └── OrderTest.java
│       │                   │   ├── repository
│       │                   │   │   └── OrderRepositoryTest.java
│       │                   │   └── service
│       │                   │       └── OrderServiceTest.java
│       │                   ├── product
│       │                   │   ├── controller
│       │                   │   │   └── ProductControllerTest.java
│       │                   │   ├── dto
│       │                   │   ├── entity
│       │                   │   │   └── ProductTest.java
│       │                   │   ├── repository
│       │                   │   │   └── ProductRepositoryTest.java
│       │                   │   └── service
│       │                   │       └── ProductServiceTest.java
│       │                   └── shared
│       │                       ├── exception
│       │                       │   └── GlobalExceptionHandlerTest.java
│       │                       └── web
│       └── resources
│           └── application-test.yml
└── target
    ├── classes
    │   ├── application-dev.yml
    │   ├── application.yml
    │   ├── db
    │   │   └── migration
    │   │       └── V1__initial_schema.sql
    │   └── io
    │       └── github
    │           └── lussac7
    │               └── cupcake
    │                   ├── CupcakeApplication.class
    │                   ├── order
    │                   │   ├── controller
    │                   │   │   └── OrderController.class
    │                   │   ├── dto
    │                   │   │   ├── OrderCreateRequest.class
    │                   │   │   ├── OrderItemRequest.class
    │                   │   │   ├── OrderItemResponse.class
    │                   │   │   └── OrderResponse.class
    │                   │   ├── entity
    │                   │   │   ├── Order.class
    │                   │   │   └── OrderItem.class
    │                   │   ├── repository
    │                   │   │   └── OrderRepository.class
    │                   │   └── service
    │                   │       └── OrderService.class
    │                   ├── product
    │                   │   ├── controller
    │                   │   │   └── ProductController.class
    │                   │   ├── dto
    │                   │   │   ├── ProductCreateRequest.class
    │                   │   │   ├── ProductResponse.class
    │                   │   │   └── ProductUpdateRequest.class
    │                   │   ├── entity
    │                   │   │   └── Product.class
    │                   │   ├── repository
    │                   │   │   └── ProductRepository.class
    │                   │   └── service
    │                   │       └── ProductService.class
    │                   └── shared
    │                       ├── config
    │                       │   ├── CorsConfig$1.class
    │                       │   └── CorsConfig.class
    │                       └── exception
    │                           ├── BusinessRuleException.class
    │                           ├── GlobalExceptionHandler.class
    │                           └── ResourceNotFoundException.class
    ├── cupcake-0.0.1-SNAPSHOT.jar
    ├── cupcake-0.0.1-SNAPSHOT.jar.original
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       ├── compile
    │       │   └── default-compile
    │       │       ├── createdFiles.lst
    │       │       └── inputFiles.lst
    │       └── testCompile
    │           └── default-testCompile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    ├── surefire-reports
    │   ├── io.github.lussac7.cupcake.dto.DTOValidationTest.txt
    │   ├── io.github.lussac7.cupcake.integration.PostgreSqlIntegrationTest.txt
    │   ├── io.github.lussac7.cupcake.order.controller.OrderControllerTest.txt
    │   ├── io.github.lussac7.cupcake.order.entity.OrderTest.txt
    │   ├── io.github.lussac7.cupcake.order.repository.OrderRepositoryTest.txt
    │   ├── io.github.lussac7.cupcake.order.service.OrderServiceTest.txt
    │   ├── io.github.lussac7.cupcake.product.controller.ProductControllerTest.txt
    │   ├── io.github.lussac7.cupcake.product.entity.ProductTest.txt
    │   ├── io.github.lussac7.cupcake.product.repository.ProductRepositoryTest.txt
    │   ├── io.github.lussac7.cupcake.product.service.ProductServiceTest.txt
    │   ├── io.github.lussac7.cupcake.shared.exception.GlobalExceptionHandlerTest.txt
    │   ├── TEST-io.github.lussac7.cupcake.dto.DTOValidationTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.integration.PostgreSqlIntegrationTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.order.controller.OrderControllerTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.order.entity.OrderTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.order.repository.OrderRepositoryTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.order.service.OrderServiceTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.product.controller.ProductControllerTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.product.entity.ProductTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.product.repository.ProductRepositoryTest.xml
    │   ├── TEST-io.github.lussac7.cupcake.product.service.ProductServiceTest.xml
    │   └── TEST-io.github.lussac7.cupcake.shared.exception.GlobalExceptionHandlerTest.xml
    └── test-classes
        ├── application-test.yml
        └── io
            └── github
                └── lussac7
                    └── cupcake
                        ├── dto
                        │   └── DTOValidationTest.class
                        ├── integration
                        │   └── PostgreSqlIntegrationTest.class
                        ├── order
                        │   ├── controller
                        │   │   └── OrderControllerTest.class
                        │   ├── entity
                        │   │   └── OrderTest.class
                        │   ├── repository
                        │   │   └── OrderRepositoryTest.class
                        │   └── service
                        │       └── OrderServiceTest.class
                        ├── product
                        │   ├── controller
                        │   │   └── ProductControllerTest.class
                        │   ├── entity
                        │   │   └── ProductTest.class
                        │   ├── repository
                        │   │   └── ProductRepositoryTest.class
                        │   └── service
                        │       └── ProductServiceTest.class
                        └── shared
                            └── exception
                                ├── GlobalExceptionHandlerTest$TestController.class
                                ├── GlobalExceptionHandlerTest$TestRequest.class
                                └── GlobalExceptionHandlerTest.class

117 directories, 146 files
```
