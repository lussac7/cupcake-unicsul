# CUPCAKE UNICSUL — Fase 1: Levantamento de Requisitos

> **Projeto:** `cupcake-unicsul`  
> **Artifact ID:** `cupcake`  
> **Group ID:** `io.github.lussac7`  
> **Idioma do projeto e da documentação:** Português brasileiro (pt-BR)  
> **Status:** Versão inicial para revisão e aprovação  
> **Fase:** 1 — Requisitos

---

## 1. Objetivo desta fase

Definir a visão do sistema, seus objetivos, partes interessadas, atores, requisitos funcionais e não funcionais, regras de negócio, requisitos de segurança, desempenho, disponibilidade, observabilidade e auditoria, restrições e critérios de aceitação.

Esta fase trata de **o que o sistema deverá fazer**, não de como será implementado. Portanto, não são definidos código Java, classes, tabelas, endpoints ou diagramas UML nesta fase.

Os requisitos usam identificadores estáveis para permitir rastreabilidade nas fases seguintes.

## 2. Identificação e contexto do projeto

| Atributo | Definição |
|---|---|
| Nome do projeto | `cupcake-unicsul` |
| Tipo | Aplicação web acadêmica para uma loja simples de cupcakes |
| `groupId` | `io.github.lussac7` |
| `artifactId` | `cupcake` |
| Linguagem backend | Java 21 LTS |
| Framework backend | Spring Boot 4.x |
| Padrão web | Spring MVC |
| Construção | Maven |
| Banco de dados | PostgreSQL |
| Persistência | Spring Data JPA / Hibernate |
| Migrações | Flyway |
| Interface | HTML, CSS e JavaScript puro (Vanilla JavaScript) |
| Testes | JUnit 5, Mockito e Testcontainers, quando apropriado |
| Modelagem | UML com PlantUML |
| Autenticação de clientes | Não será implementada |
| Natureza | Trabalho acadêmico |

A linha tecnológica acima é a estabelecida pelo prompt mestre. As versões específicas das dependências deverão ser selecionadas de forma compatível com a versão escolhida do Spring Boot 4.x, na fase apropriada.

## 3. Visão do sistema

O Cupcake Unicsul será uma aplicação web simples que permitirá a visitantes consultar um catálogo de cupcakes, visualizar detalhes dos produtos, montar um carrinho de compras e realizar pedidos informando seus dados e endereço de entrega.

O sistema também deverá oferecer funcionalidades básicas de manutenção do catálogo. Como o trabalho não exige autenticação, as operações administrativas não terão controle de acesso nesta versão acadêmica.

O foco do projeto é demonstrar, de maneira incremental e compreensível, engenharia de requisitos, modelagem UML, arquitetura em camadas, desenvolvimento web com Spring MVC, persistência relacional e testes automatizados.

## 4. Objetivos do sistema

| ID | Objetivo |
|---|---|
| OBJ-001 | Disponibilizar um catálogo online de cupcakes. |
| OBJ-002 | Permitir que visitantes consultem informações dos produtos. |
| OBJ-003 | Permitir a seleção de produtos e o gerenciamento de um carrinho. |
| OBJ-004 | Permitir que visitantes registrem pedidos sem criar uma conta. |
| OBJ-005 | Registrar e manter os pedidos no banco de dados. |
| OBJ-006 | Disponibilizar manutenção básica do catálogo de produtos. |
| OBJ-007 | Demonstrar boas práticas de análise, projeto, implementação e testes de software. |

## 5. Partes interessadas

| ID | Parte interessada | Interesse / responsabilidade |
|---|---|---|
| STK-001 | Universidade | Avaliar o trabalho acadêmico e seus artefatos. |
| STK-002 | Aluno / desenvolvedor | Analisar, projetar, implementar, testar e documentar o sistema. |
| STK-003 | Cliente / visitante | Consultar produtos e realizar pedidos. |
| STK-004 | Responsável pela loja | Manter o catálogo e consultar os pedidos recebidos. |

## 6. Atores

### ACT-001 — Cliente (visitante)

Pessoa que acessa a loja sem autenticação.

Pode consultar o catálogo, visualizar produtos, montar o carrinho, informar dados para entrega, confirmar um pedido e consultar um pedido por seu identificador, caso essa funcionalidade seja aprovada no escopo.

### ACT-002 — Administrador da loja

Pessoa responsável pela manutenção do catálogo e pela consulta dos pedidos.

Nesta versão acadêmica, não haverá autenticação nem autorização. Assim, as operações administrativas não estarão protegidas. Essa simplificação é uma decisão de escopo acadêmico e não representa uma configuração adequada para uma loja real.

## 7. Requisitos funcionais

### 7.1 Catálogo de produtos

| ID | Requisito |
|---|---|
| FR-001 | O sistema deverá apresentar o catálogo de cupcakes disponíveis. |
| FR-002 | O sistema deverá permitir a visualização dos detalhes de um produto. |
| FR-003 | Os detalhes de um produto deverão incluir, no mínimo, nome, descrição e preço. |
| FR-004 | O sistema poderá apresentar uma imagem do produto quando houver imagem cadastrada. |
| FR-005 | O catálogo público deverá distinguir produtos disponíveis de produtos indisponíveis e não deverá permitir a compra de produtos indisponíveis. |
| FR-006 | O administrador deverá poder cadastrar produtos. |
| FR-007 | O administrador deverá poder alterar os dados de produtos cadastrados. |
| FR-008 | O administrador deverá poder tornar um produto indisponível ou removê-lo do catálogo. |

### 7.2 Carrinho de compras

| ID | Requisito |
|---|---|
| FR-009 | O cliente deverá poder adicionar um produto disponível ao carrinho. |
| FR-010 | O cliente deverá poder alterar a quantidade de um item do carrinho. |
| FR-011 | O cliente deverá poder remover um item do carrinho. |
| FR-012 | O sistema deverá apresentar os itens e as respectivas quantidades no carrinho. |
| FR-013 | O sistema deverá calcular o subtotal de cada item. |
| FR-014 | O sistema deverá calcular e apresentar o valor total do carrinho. |
| FR-015 | O sistema deverá impedir quantidades iguais ou inferiores a zero. |
| FR-016 | O sistema deverá validar novamente a disponibilidade e o preço dos produtos durante a confirmação do pedido. |

### 7.3 Identificação do cliente e entrega

| ID | Requisito |
|---|---|
| FR-017 | O cliente deverá informar seu nome para realizar um pedido. |
| FR-018 | O cliente deverá informar um endereço de e-mail válido para realizar um pedido. |
| FR-019 | O cliente deverá informar os dados necessários para a entrega. |
| FR-020 | O sistema deverá apresentar um resumo do pedido antes de sua confirmação. |

### 7.4 Pedidos

| ID | Requisito |
|---|---|
| FR-021 | O cliente deverá poder confirmar um pedido com pelo menos um item válido. |
| FR-022 | O sistema deverá gerar um identificador único para cada pedido. |
| FR-023 | O sistema deverá registrar os dados do cliente, os itens, as quantidades, os preços unitários no momento da compra e o valor total do pedido. |
| FR-024 | O sistema deverá persistir o pedido e seus itens. |
| FR-025 | O sistema deverá apresentar uma confirmação após o registro bem-sucedido do pedido. |
| FR-026 | O sistema deverá permitir a consulta de um pedido por seu identificador, sujeita à decisão de escopo registrada na seção 17. |
| FR-027 | O administrador deverá poder consultar os pedidos recebidos. |

## 8. Requisitos não funcionais

### 8.1 Tecnologia e plataforma

| ID | Requisito |
|---|---|
| NFR-001 | O backend deverá ser desenvolvido com Java 21 LTS. |
| NFR-002 | O backend deverá utilizar Spring Boot 4.x. |
| NFR-003 | O projeto deverá utilizar Maven para gerenciamento de dependências e construção. |
| NFR-004 | A camada web deverá utilizar Spring MVC. |
| NFR-005 | O banco de dados relacional deverá ser PostgreSQL. |
| NFR-006 | A persistência deverá utilizar Spring Data JPA e Hibernate. |
| NFR-007 | As alterações do esquema do banco deverão ser gerenciadas pelo Flyway. |
| NFR-008 | A interface deverá utilizar HTML, CSS e JavaScript puro. |
| NFR-009 | Os testes automatizados deverão utilizar JUnit 5. |
| NFR-010 | Mockito e Testcontainers deverão ser utilizados quando forem adequados ao tipo de teste. |
| NFR-011 | Os diagramas UML deverão ser produzidos com PlantUML. |
| NFR-012 | O código-fonte e a documentação deverão ser versionados com Git. |

### 8.2 Arquitetura e qualidade

| ID | Requisito |
|---|---|
| NFR-013 | A aplicação deverá adotar a arquitetura de Monólito Modular com separação em camadas. |
| NFR-014 | As responsabilidades de apresentação, aplicação/serviços, domínio e persistência deverão ser separadas. |
| NFR-015 | As dependências entre componentes deverão ser explícitas. |
| NFR-016 | A injeção de dependências deverá utilizar preferencialmente injeção por construtor. |
| NFR-017 | DTOs deverão ser utilizados nas fronteiras da API. |
| NFR-018 | Entidades JPA não deverão ser expostas diretamente em respostas REST. |
| NFR-019 | Regras de negócio e invariantes deverão ser validadas na camada apropriada. |
| NFR-020 | O projeto deverá evitar abstrações, frameworks e complexidade que não sejam justificados pelos requisitos. |
| NFR-021 | A aplicação deverá possuir tratamento consistente de erros e registros de log significativos. |
| NFR-022 | A construção e a execução dos testes deverão ser reproduzíveis por meio do Maven. |

### 8.3 Usabilidade e localização

| ID | Requisito |
|---|---|
| NFR-023 | A interface deverá ser simples e intuitiva. |
| NFR-024 | Os valores monetários deverão ser apresentados em reais (BRL), no padrão brasileiro. |
| NFR-025 | As mensagens e os textos da interface deverão estar em português brasileiro. |
| NFR-026 | A interface deverá fornecer retorno compreensível após ações relevantes do usuário. |
| NFR-027 | Dados inválidos deverão gerar mensagens de validação compreensíveis. |

## 9. Regras de negócio

| ID | Regra |
|---|---|
| BR-001 | Todo produto deverá possuir um identificador único. |
| BR-002 | Todo produto deverá possuir nome não vazio. |
| BR-003 | O preço de um produto deverá ser maior que zero. |
| BR-004 | Somente produtos disponíveis poderão ser adicionados ao carrinho e incluídos em novos pedidos. |
| BR-005 | A quantidade de cada item do carrinho deverá ser um número inteiro positivo. |
| BR-006 | O subtotal de um item será o preço unitário multiplicado pela quantidade. |
| BR-007 | O total do carrinho será a soma dos subtotais de seus itens. |
| BR-008 | Um pedido deverá conter pelo menos um item. |
| BR-009 | Um pedido deverá possuir os dados obrigatórios do cliente e da entrega. |
| BR-010 | O valor total do pedido deverá ser calculado pelo sistema, e não aceito como valor confiável enviado pelo navegador. |
| BR-011 | O pedido deverá preservar o nome do produto e o preço unitário praticado no momento da compra. |
| BR-012 | Alterações posteriores no catálogo não deverão modificar os dados históricos dos itens de pedidos já registrados. |
| BR-013 | O identificador de cada pedido deverá ser único. |
| BR-014 | O registro do pedido e de seus itens deverá ocorrer de forma consistente: não deverá existir pedido confirmado sem seus respectivos itens persistidos. |
| BR-015 | A disponibilidade e o preço dos produtos deverão ser verificados novamente no momento da confirmação do pedido. |

## 10. Requisitos de segurança

A aplicação não exigirá login de clientes e não implementará autenticação ou autorização nesta versão.

Mesmo sem autenticação, deverão ser observadas práticas básicas de segurança:

| ID | Requisito |
|---|---|
| SEC-001 | O cliente deverá poder navegar e realizar pedidos sem autenticação. |
| SEC-002 | O sistema não deverá implementar cadastro de contas, senhas ou login nesta versão. |
| SEC-003 | Os dados recebidos deverão ser validados antes do processamento. |
| SEC-004 | As operações de persistência deverão utilizar os mecanismos seguros do framework, evitando a montagem insegura de comandos SQL. |
| SEC-005 | Credenciais do banco e outros segredos não deverão ser armazenados no repositório Git. |
| SEC-006 | Respostas de erro não deverão expor stack traces, credenciais ou detalhes internos da aplicação. |
| SEC-007 | Dados pessoais do cliente deverão ser apresentados somente quando necessários à funcionalidade. |
| SEC-008 | A ausência de autenticação nas operações administrativas deverá ser documentada como limitação conhecida do trabalho acadêmico. |

## 11. Requisitos de desempenho

Os valores abaixo são metas iniciais para condições normais de demonstração acadêmica; não representam garantias para cargas arbitrárias.

| ID | Requisito |
|---|---|
| PERF-001 | Consultas comuns ao catálogo deverão responder, como meta, em até 2 segundos. |
| PERF-002 | Operações comuns do carrinho deverão responder, como meta, em até 2 segundos. |
| PERF-003 | A confirmação de um pedido deverá responder, como meta, em até 3 segundos, desconsiderando indisponibilidade externa. |
| PERF-004 | Consultas ao banco deverão buscar apenas os dados necessários à operação solicitada. |

## 12. Requisitos de disponibilidade e confiabilidade

| ID | Requisito |
|---|---|
| AVL-001 | A aplicação deverá funcionar quando o backend e o banco de dados estiverem disponíveis. |
| AVL-002 | Falhas de conexão com o banco deverão ser tratadas sem encerramento descontrolado da aplicação. |
| AVL-003 | Quando uma operação não puder ser concluída, o sistema deverá apresentar uma mensagem adequada. |
| AVL-004 | Uma falha durante a gravação de um pedido não deverá deixar um pedido parcialmente persistido. |

Alta disponibilidade, replicação, recuperação de desastre e operação distribuída estão fora do escopo acadêmico inicial.

## 13. Requisitos de observabilidade

| ID | Requisito |
|---|---|
| OBS-001 | A aplicação deverá registrar eventos relevantes por meio de logs. |
| OBS-002 | Erros inesperados deverão ser registrados com contexto suficiente para diagnóstico, sem expor dados sensíveis desnecessariamente. |
| OBS-003 | A criação de um pedido deverá gerar um evento de log identificável pelo identificador do pedido. |
| OBS-004 | Problemas de conectividade com o banco deverão ser identificáveis nos logs. |

## 14. Requisitos de auditoria e histórico

| ID | Requisito |
|---|---|
| AUD-001 | Cada pedido deverá registrar sua data e hora de criação. |
| AUD-002 | Os itens do pedido deverão preservar os dados relevantes do produto no momento da compra. |
| AUD-003 | Cada pedido deverá possuir identificador que permita sua localização. |
| AUD-004 | O histórico de pedidos deverá manter os valores originalmente registrados, independentemente de alterações posteriores no catálogo. |

Um histórico completo de ações administrativas não faz parte do escopo inicial.

## 15. Restrições do projeto

| ID | Restrição |
|---|---|
| CON-001 | O sistema é um trabalho acadêmico. |
| CON-002 | O projeto deverá utilizar Java 21. |
| CON-003 | O projeto deverá utilizar Spring Boot 4.x e Spring MVC. |
| CON-004 | Maven deverá ser utilizado como ferramenta de build. |
| CON-005 | PostgreSQL deverá ser o banco de dados relacional. |
| CON-006 | A persistência deverá utilizar Spring Data JPA / Hibernate. |
| CON-007 | Flyway deverá gerenciar as migrações do banco. |
| CON-008 | JUnit 5 deverá ser utilizado nos testes automatizados. |
| CON-009 | PlantUML deverá ser utilizado para os diagramas UML. |
| CON-010 | Não será exigida autenticação de clientes. |
| CON-011 | O desenvolvimento deverá seguir as fases definidas no prompt mestre. |
| CON-012 | A implementação deverá ser incremental, arquivo por arquivo quando houver geração de código. |
| CON-013 | A documentação produzida neste projeto deverá ser escrita em português brasileiro, conforme orientação do aluno. |

## 16. Critérios de aceitação iniciais

| ID | Critério |
|---|---|
| AC-001 | Um visitante consegue acessar o catálogo sem realizar login. |
| AC-002 | Um visitante consegue visualizar os detalhes de um cupcake. |
| AC-003 | Um visitante consegue adicionar ao carrinho um produto disponível. |
| AC-004 | Um visitante consegue alterar a quantidade e remover itens do carrinho. |
| AC-005 | Os subtotais e o total do carrinho são calculados corretamente. |
| AC-006 | O sistema impede a confirmação de um carrinho vazio. |
| AC-007 | O visitante consegue informar os dados obrigatórios para o pedido e a entrega. |
| AC-008 | O sistema gera um identificador único para o pedido confirmado. |
| AC-009 | O pedido e seus itens são persistidos de forma consistente no PostgreSQL. |
| AC-010 | O pedido preserva os preços e as informações relevantes dos produtos no momento da compra. |
| AC-011 | O visitante recebe uma confirmação após a conclusão do pedido. |
| AC-012 | Dados inválidos são rejeitados com mensagens compreensíveis. |
| AC-013 | O projeto pode ser construído e seus testes executados por meio do Maven. |
| AC-014 | As operações administrativas básicas de catálogo podem ser executadas sem login, conforme a limitação acadêmica documentada. |

## 17. Decisões de escopo pendentes de aprovação

As decisões abaixo não estão especificadas no prompt mestre. São propostas para tornar os requisitos mais objetivos; devem ser confirmadas antes de serem tratadas como decisões definitivas.

| ID | Questão | Proposta inicial |
|---|---|---|
| DEC-001 | O cliente poderá comprar sem cadastro? | Sim. O cliente informa seus dados a cada pedido. |
| DEC-002 | Haverá pagamento online? | Não. O sistema registra o pedido, mas não processa pagamentos. |
| DEC-003 | Haverá manutenção do catálogo? | Sim. CRUD básico de produtos para fins acadêmicos. |
| DEC-004 | Como o carrinho será mantido? | Armazenamento temporário no navegador, por exemplo `sessionStorage`, sujeito à validação na fase de arquitetura. |
| DEC-005 | O cliente poderá consultar um pedido depois? | Proposta: sim, por identificador do pedido. É necessário decidir se isso será público ou exigirá alguma informação adicional de conferência. |
| DEC-006 | O administrador poderá consultar pedidos? | Proposta: sim, sem autenticação nesta versão acadêmica. |
| DEC-007 | Como tratar a remoção de produtos já utilizados em pedidos? | Proposta: preferir marcar o produto como indisponível/inativo, preservando o histórico dos pedidos. |

## 18. Fora do escopo inicial

- Cadastro de contas de clientes.
- Login, senhas, autenticação e autorização.
- Gateway ou processamento de pagamentos.
- Integração com empresas de entrega.
- Rastreamento de entregas.
- Programa de fidelidade.
- Cupons, descontos e campanhas promocionais.
- Controle avançado de estoque.
- Múltiplas lojas ou filiais.
- Alta disponibilidade e arquitetura de microsserviços.

## 19. Rastreabilidade dos requisitos

Nas fases seguintes, os requisitos serão relacionados aos artefatos correspondentes:

| Artefato futuro | Origem principal |
|---|---|
| Diagrama de Casos de Uso | Atores e requisitos funcionais |
| Descrições de Casos de Uso | Requisitos funcionais e regras de negócio |
| Diagramas de Arquitetura | Requisitos não funcionais e restrições |
| Modelo de Domínio | Requisitos funcionais e regras de negócio |
| Diagrama de Classes | Modelo de domínio e responsabilidades arquiteturais |
| Diagrama Entidade-Relacionamento | Modelo de domínio e requisitos de persistência |
| Migrações Flyway | Modelo relacional aprovado |
| Testes unitários | Regras de negócio e comportamentos dos requisitos |
| Testes web e de integração | Contratos da aplicação, persistência e critérios de aceitação |

Os identificadores definidos nesta fase deverão ser mantidos nas próximas fases. Caso um requisito seja alterado, sua mudança deverá ser explicitada e seus impactos nos artefatos relacionados deverão ser avaliados.

## 20. Estado da Fase 1

| Entrega | Estado |
|---|---|
| Visão do sistema | Definida inicialmente |
| Objetivos | Definidos inicialmente |
| Partes interessadas | Identificadas |
| Atores | Identificados |
| Requisitos funcionais | Definidos inicialmente |
| Requisitos não funcionais | Definidos inicialmente |
| Regras de negócio | Definidas inicialmente |
| Requisitos de segurança | Definidos inicialmente |
| Requisitos de desempenho | Definidos inicialmente |
| Requisitos de disponibilidade | Definidos inicialmente |
| Requisitos de observabilidade | Definidos inicialmente |
| Requisitos de auditoria | Definidos inicialmente |
| Restrições | Definidas |
| Critérios de aceitação | Definidos inicialmente |
| Decisões de escopo | Aguardando aprovação |
| Aprovação da Fase 1 | Pendente |

### Próxima ação

Revisar e aprovar os requisitos e as decisões pendentes da seção 17. Após a aprovação, a Fase 1 poderá ser considerada concluída e o projeto poderá avançar para a **Fase 2 — Casos de Uso**, com descrições textuais e diagramas PlantUML.

---

**Nota de controle:** Este documento registra a linha de base proposta para os requisitos. Não autoriza ainda a implementação. Nenhum código Java, esquema de banco de dados ou diagrama UML foi produzido nesta fase.
