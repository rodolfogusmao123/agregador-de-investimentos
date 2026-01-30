# 📈 Agregador de Investimentos API - Backend

![Status do Projeto](https://img.shields.io/badge/status-active-brightgreen)
![Java Version](https://img.shields.io/badge/java-21-orange)
![Spring Boot](https://img.shields.io/badge/spring--boot-4.0.1-brightgreen)

Esta é uma API RESTful robusta desenvolvida para auxiliar investidores na gestão de seus ativos. O sistema permite a criação de contas, associação de ações e o cálculo automático do patrimônio total baseado em dados em tempo real.

---

## 🖼️ Arquitetura e Modelo de Dados

Abaixo está o diagrama que ilustra o relacionamento entre as entidades do sistema (Usuários, Contas, Ações e Endereços).

![Diagrama de Relacionamento](agregador-de-investimentos-MER.png)

---

## 🚀 Tecnologias Utilizadas

* **Java 21** & **Spring Boot 4.0.1**: Base do ecossistema.
* **Spring Security & JWT**: Autenticação stateless e autorização baseada em Roles (ADMIN/USER).
* **Spring Data JPA e Hibernate**: Abstração de persistência com suporte a diversos bancos de dados.
* **OpenFeign**: Cliente HTTP declarativo para integração com a API **Brapi**.
* **Lombok**: Redução de código boilerplate.
* **Resilience4j**: Implementação de *Circuit Breaker* para garantir disponibilidade caso a API externa falhe.
* **JUnit 5 & Mockito**: Testes de unidade e mocks de serviços.
* **Slf4j**: Logs personalizados durante a execução.
* **Postgres**: Banco de dados relacional robusto para armazenamento de usuários e ativos.
* **Docker e Docker Compose**: Containerização completa da aplicação e do banco de dados para deploy simplificado.
* **SpringDoc OpenAPI (Swagger)**: Documentação interativa da API com schemas detalhados de sucesso e erro.
* **Spring Boot Actuator**: Exposição de endpoints operacionais para monitorar a saúde da aplicação (/health), métricas e informações do sistema.
* **Micrometer & Prometheus**: Coleta e exportação de métricas customizadas e de infraestrutura no formato compatível com o Prometheus, permitindo monitorar o consumo de CPU, memória e tempo de resposta das requisições.
* **Global Exception Handling**: Tratamento padronizado de erros com ErrorResponseDto.
---

## 🔒 Funcionalidades de Segurança (Destaques)

### 🛡️ Hashing de Senhas
As senhas nunca são armazenadas em texto plano. Utilizamos o **BCryptPasswordEncoder** tanto na criação quanto na atualização do perfil.

### 🚫 Global Exception Handler
Tratamento centralizado de erros que fornece respostas claras e seguras via DTOs, evitando vazamento de stacktraces do servidor.

---
## 📊 Principais Endpoints (API Reference)

A documentação completa e interativa (Swagger UI) pode ser acessada em: `http://localhost:8080/swagger-ui.html`.

### 🔐 Autenticação (Acesso Público)
| Método | Endpoint         | Descrição                                |
|:-------|:-----------------|:-----------------------------------------|
| `POST` | `/auth/register` | Registra um novo usuário no sistema.     |
| `POST` | `/auth/login`    | Autentica e retorna um Bearer Token JWT. |
| `GET`  | `/auth/me`       | Retorna os dados do usuário autenticado. |

### 👤 Usuários (Requer Autenticação)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/users/{userId}` | Retorna os detalhes de um usuário específico por ID. |
| `GET` | `/users/all` | Lista todos os usuários cadastrados (Acesso: ADMIN). |
| `PUT` | `/users/{userId}` | Atualiza as informações do perfil do usuário. |
| `DELETE` | `/users/{userId}` | Remove permanentemente a conta do usuário do sistema. |

### 💳 Contas & Carteiras (Requer Autenticação)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/users/{userId}/accounts` | Cria uma nova carteira de investimentos para o usuário. |
| `GET` | `/users/{userId}/accounts` | Lista todas as contas/carteiras vinculadas ao usuário. |
| `GET` | `/accounts/{accountId}/balance` | **Cálculo de Patrimônio:** Soma o valor em tempo real de todos os ativos da conta via API externa. |

### 📈 Ativos & Investimentos (Requer Autenticação)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/stocks` | Cadastra um novo ativo (Ação/FII) no catálogo do sistema. |
| `POST` | `/accounts/{accountId}/stocks` | Associa/Compra um ativo para uma conta específica (vínculo Account-Stock). |
| `GET` | `/accounts/{accountId}/stocks` | Lista todos os ativos e quantidades presentes em uma carteira específica. |
---

## ⚙️ Configuração e Execução

### 1. Requisitos Prévios
* **Java 21** e **Maven** (para execução local).
* **Docker & Docker Compose** (para execução via containers).
* **Token Brapi**: Obtenha sua chave gratuita em [brapi.dev](https://brapi.dev).

### 2. Variáveis de Ambiente
O projeto utiliza variáveis de ambiente para proteger dados sensíveis. Se estiver utilizando o **IntelliJ IDEA**, você pode configurá-las facilmente:
1. Vá ao menu `Run` > `Edit Configurations...`.
2. No campo **Environment Variables**, adicione as seguintes chaves:
   ```env
   TOKEN=seu_token_aqui;JWT_SECRET=sua_chave_secreta_segura

### 3. Execução com Docker 🐳
A aplicação está preparada para rodar em containers, gerenciando a API, o banco de dados PostgreSQL e o monitoramento automaticamente.

```bash
# Na raiz do projeto, execute o comando abaixo para subir o ecossistema:
docker-compose up --build
```

### 4. Acesso ao Banco de Dados (PostgreSQL)
Caso precise validar dados ou realizar queries manualmente, você pode acessar o terminal do Postgres diretamente pelo container Docker:
```bash
# 1. Acesse o terminal interativo do container de banco de dados
docker exec -it agregador-de-investimento psql -U postgres -d db_investimentos

# 2. Comandos úteis dentro do terminal psql:
\dt                  -> Lista todas as tabelas (users, accounts, stocks, etc.)
SELECT * FROM users; -> Visualiza os usuários cadastrados
\q                   -> Sai do terminal do banco de dados.
```

### 5. Configuração do application.properties
Para desenvolvimento local sem Docker (ex: usando banco H2 ou Postgres local), certifique-se de que as propriedades abaixo apontem para o seu ambiente no arquivo ```src/main/resources/application.properties```:
```bash
# Brapi API Token (Configurado via Variável de Ambiente no IntelliJ)
TOKEN=${TOKEN}

# JWT Security
api.security.token.secret=${JWT_SECRET:minha-chave-secreta-padrao}

# Database (Exemplo PostgreSQL Local)
spring.datasource.url=jdbc:postgresql://localhost:5432/investdb
spring.datasource.username=postgres
spring.datasource.password=sua_senha

# Observabilidade (Actuator & Prometheus)
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=always
```

## 🧪 Testes e Qualidade

O projeto utiliza as principais bibliotecas do ecossistema Java para garantir que a lógica de negócio e as camadas de segurança funcionem conforme o esperado.

### 🛠️ Tecnologias de Teste
* **JUnit 5**: Framework principal para execução dos testes unitários e de integração.
* **Mockito**: Utilizado para criação de mocks, permitindo isolar a lógica de serviço das dependências de banco de dados e APIs externas.
* **AssertJ**: Biblioteca para asserções fluídas e de fácil leitura.
* **Spring Security Test**: Ferramentas específicas para simular usuários autenticados e testar permissões de rotas.

### 📂 Estrutura de Testes
Os testes estão localizados em `src/test/java/` e seguem a hierarquia dos pacotes da aplicação:

1. **Unit Tests (Services)**: Validação de regras de negócio, como o cálculo de patrimônio e validação de senhas no `AuthService`.
2. **Integration Tests (Controllers)**: Garantem que os endpoints estão respondendo corretamente (status 200, 201, 401, 403) e que o `GlobalExceptionHandler` está capturando os erros.

### 🚀 Como Executar os Testes

Você pode rodar todos os testes através da sua IDE (IntelliJ) ou via terminal usando o Maven:

```bash
# Executar todos os testes
mvn test

# Executar um teste específico
mvn test -Dtest=AuthServiceTest
```

## ⚙️ CI/CD (Integração Contínua)

O projeto utiliza **GitHub Actions** para garantir a qualidade do código em cada contribuição. O pipeline é executado automaticamente em cada `push` ou `pull request` para a branch `main`.

* **Build & Test**: O workflow compila o projeto usando Java 21 e executa toda a suíte de testes unitários e de integração.
* **Segurança**: Garante que novas alterações não quebrem o fluxo de autenticação JWT ou o tratamento global de exceções.
* **Status**: O selo de "Build" no topo do repositório indica se a versão atual está estável.

