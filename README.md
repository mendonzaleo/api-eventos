Projeto desenvolvido com foco em aprendizado e prática para atuação como desenvolvedor backend.

# 📅 API de Eventos

API REST para gerenciamento de eventos e usuários, desenvolvida com **Spring Boot 3.5.6** e **Java 17**.

Permite criar eventos, gerenciar convidados e controlar o acesso por perfis de usuário, com documentação automática via Swagger UI.

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Executando o Projeto](#-executando-o-projeto)
- [Documentação da API](#-documentação-da-api)
- [Endpoints](#-endpoints)
- [Autenticação](#-autenticação)
- [Perfis de Acesso](#-perfis-de-acesso)
- [Modelagem](#-modelagem)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Roadmap](#-roadmap)

---

## 🛠 Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| Java | 17 (LTS) | Linguagem principal |
| Spring Boot | 3.5.6 | Framework base da aplicação |
| Spring Data JPA | (gerenciada) | Camada de persistência com Hibernate |
| Spring Security | (gerenciada) | Autenticação e autorização |
| PostgreSQL | (gerenciada) | Banco de dados relacional |
| Lombok | (gerenciada) | Redução de boilerplate (getters, setters) |
| SpringDoc OpenAPI | 2.8.16 | Geração automática do Swagger UI |
| Jakarta Validation | 3.0.2 | Validação de dados de entrada |
| jjwt-api / jjwt-impl / jjwt-jackson | 0.12.6 | Geração e validação de tokens JWT |
| Maven | (wrapper incluso) | Gerenciamento de dependências e build |

---

## ✅ Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Java 17+** — [Download](https://adoptium.net/)
- **PostgreSQL 13+** — [Download](https://www.postgresql.org/download/)
- **Maven 3.8+** *(opcional — o projeto inclui o Maven Wrapper `mvnw`)*
- **Git**

---

## ⚙️ Configuração do Ambiente

### 1. Clone o repositório

```bash
git clone https://github.com/mendonzaleo/api-eventos.git
cd api-eventos
```

### 2. Crie o banco de dados no PostgreSQL

Conecte-se ao PostgreSQL e execute:

```sql
CREATE DATABASE db_events;
```

### 3. Configure as credenciais do banco

Abra o arquivo `src/main/resources/application.properties` e ajuste as propriedades conforme seu ambiente:

```properties
spring.application.name=events

# Porta da aplicação
server.port=9090

# Conexão com o PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/db_events
spring.datasource.username=postgres
spring.datasource.password=admin

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Pool de conexões
spring.datasource.hikari.maximum-pool-size=5

# JWT
jwt.secret=sua-chave-secreta-minimo-32-caracteres-aqui
jwt.expiration=3600000

# Logging
logging.level.root=INFO
logging.level.org.springframework=DEBUG
logging.level.com.meus.eventos=TRACE
```

> ⚠️ **Atenção:** nunca suba credenciais reais para o repositório. O `jwt.secret` deve ter no mínimo 32 caracteres (256 bits). Em produção, utilize variáveis de ambiente:
>
> ```properties
> jwt.secret=${JWT_SECRET}
> jwt.expiration=${JWT_EXPIRATION:3600000}
> ```

---

## ▶️ Executando o Projeto

### Usando o Maven Wrapper (recomendado)

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### Usando Maven instalado globalmente

```bash
mvn spring-boot:run
```

### Gerando e executando o JAR

```bash
# Build
./mvnw clean package -DskipTests

# Execução
java -jar target/events-0.0.1-SNAPSHOT.jar
```

Após a inicialização, a aplicação estará disponível em:

```
http://localhost:9090
```

---

## 📖 Documentação da API

A documentação interativa é gerada automaticamente pelo **SpringDoc OpenAPI (Swagger UI)**.

Acesse após iniciar a aplicação:

```
http://localhost:9090/swagger-ui/index.html
```

No Swagger UI você pode visualizar todos os endpoints, seus parâmetros, modelos de requisição e resposta, e executar chamadas diretamente pelo navegador. A autenticação pode ser feita de duas formas:

- **Basic Auth:** clique em **Authorize**, selecione *BasicAuth* e informe username e senha.
- **JWT:** obtenha um token via `POST /auth/login`, clique em **Authorize**, selecione *BearerAuth* e cole o token no campo `Value` no formato `Bearer <token>`.

---

## 🔗 Endpoints

### Autenticação — `/auth`

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| `POST` | `/auth/login` | Autentica o usuário e retorna um token JWT | ❌ Não |

#### Exemplo — Login e obtenção do token

**Request:**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "lmendonza",
  "senha": "minhasenha123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsbWVuZG9uemEi...",
  "tipo": "Bearer",
  "expiracao": "2025-10-20T16:30:00"
}
```

Use o token retornado nas próximas requisições via header:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### Eventos — `/eventos`

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| `GET` | `/eventos` | Lista todos os eventos (ordenados por data, mais recentes primeiro) | ✅ Sim |
| `GET` | `/eventos/{data}` | Lista eventos por data de agendamento (`yyyy-MM-dd`) | ✅ Sim |
| `POST` | `/eventos` | Cria um novo evento | ✅ Sim |
| `DELETE` | `/eventos/{id}` | Remove um evento pelo ID | ✅ Sim |
| `GET` | `/eventos/convidados/{idEvento}` | Lista os convidados de um evento | ✅ Sim |
| `POST` | `/eventos/convidados/{id}` | Adiciona um convidado ao evento pelo username | ✅ Sim |
| `DELETE` | `/eventos/convidados/{id}` | Remove um convidado do evento pelo nome | ✅ Sim |

#### Exemplo — Criar evento (com Basic Auth)

**Request:**
```http
POST /eventos
Content-Type: application/json
Authorization: Basic dXN1YXJpbzpzZW5oYQ==

{
  "nome": "Workshop de Spring Boot",
  "localizacao": "Auditório B, Bloco 2",
  "dataAgendamento": "2025-09-15"
}
```

#### Exemplo — Criar evento (com JWT)

**Request:**
```http
POST /eventos
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "nome": "Workshop de Spring Boot",
  "localizacao": "Auditório B, Bloco 2",
  "dataAgendamento": "2025-09-15"
}
```

**Response `201 Created`:**
```json
{
  "nome": "Workshop de Spring Boot",
  "localizacao": "Auditório B, Bloco 2",
  "dataAgendamento": "2025-09-15"
}
```

---

### Usuários — `/usuarios`

| Método | Endpoint | Descrição | Auth | Perfil Necessário |
|--------|----------|-----------|------|-------------------|
| `GET` | `/usuarios` | Lista todos os usuários | ✅ Sim | Qualquer |
| `GET` | `/usuarios/{id}` | Busca usuário por ID | ✅ Sim | Qualquer |
| `POST` | `/usuarios` | Cria um novo usuário | ✅ Sim | Qualquer |
| `PUT` | `/usuarios/{id}` | Atualiza dados de um usuário | ✅ Sim | Qualquer |
| `DELETE` | `/usuarios/{id}` | Remove um usuário | ✅ Sim | `MANAGERS` |

#### Exemplo — Criar usuário

**Request:**
```http
POST /usuarios
Content-Type: application/json

{
  "nome": "Leonardo Mendonza",
  "username": "lmendonza",
  "senha": "minhasenha123"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "nome": "Leonardo Mendonza",
  "username": "lmendonza"
}
```

> 🔒 A senha nunca é retornada nas respostas da API.

---

## 🔐 Autenticação

A API suporta dois métodos de autenticação que coexistem. O cliente pode escolher qual utilizar em cada requisição.

---

### Método 1 — HTTP Basic Auth

Envie as credenciais codificadas em Base64 no header de cada requisição:

```http
Authorization: Basic <base64(username:senha)>
```

Exemplo com `curl`:

```bash
curl -u lmendonza:minhasenha123 http://localhost:9090/eventos
```

> ⚠️ **Importante:** Basic Auth transmite credenciais em Base64, que é reversível. Em produção, **sempre use HTTPS**.

---

### Método 2 — JWT (JSON Web Token)

**Passo 1 — Obter o token:**

```http
POST /auth/login
Content-Type: application/json

{
  "username": "lmendonza",
  "senha": "minhasenha123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiracao": "2025-10-20T16:30:00"
}
```

**Passo 2 — Usar o token nas requisições:**

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Exemplo com `curl`:

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
     http://localhost:9090/eventos
```

**Configuração do token (`application.properties`):**

| Propriedade | Descrição |
|---|---|
| `jwt.secret` | Chave de assinatura do token (mínimo 32 caracteres) |
| `jwt.expiration` | Tempo de vida em milissegundos (`3600000` = 1 hora) |

> 💡 O token expira após o tempo configurado. Após a expiração, o cliente deve autenticar novamente em `POST /auth/login` para obter um novo token.

---

### Como os dois métodos coexistem

O `JwtAuthFilter` verifica se a requisição contém um header `Authorization: Bearer ...`. Se sim, valida o token JWT e autentica o usuário. Caso contrário, o filtro é ignorado e o Spring Security processa normalmente via Basic Auth. Cada requisição é autenticada de forma independente — a API é completamente stateless.

---

## 👥 Perfis de Acesso

A aplicação possui dois perfis de usuário, armazenados na tabela `tab_user_roles`:

| Perfil | Permissões |
|--------|-----------|
| `ROLE_USER` | Acesso geral à API (leitura e escrita em eventos e usuários) |
| `ROLE_MANAGERS` | Todas as permissões de `ROLE_USER` + exclusão de usuários (`DELETE /usuarios/{id}`) |

Ao criar um usuário via `POST /usuarios`, o perfil `ROLE_USER` é atribuído automaticamente.

Para conceder o perfil `MANAGERS`, insira diretamente no banco:

```sql
INSERT INTO tab_user_roles (user_id, role_id)
VALUES (<id_do_usuario>, 'MANAGERS');
```

---

## 📐 Modelagem

### Diagrama de Entidade-Relacionamento

```mermaid
erDiagram
    EVENTOS {
        INT id PK
        VARCHAR name
        VARCHAR location
        DATE scheduleDate
    }

    TAB_USER {
        INT id_user PK
        VARCHAR name
        VARCHAR username
        VARCHAR password
    }

    TAB_USER_ROLES {
        INT user_id FK
        VARCHAR role_id
    }

    USUARIOS_EVENTOS {
        INT usuario_id FK
        INT evento_id FK
    }

    EVENTOS ||--o{ USUARIOS_EVENTOS : "possui"
    TAB_USER ||--o{ USUARIOS_EVENTOS : "participa de"
    TAB_USER ||--o{ TAB_USER_ROLES : "possui"
```

---

### Diagrama de Classes

```mermaid
classDiagram
    class Evento {
        +Integer id
        +String nome
        +String localizacao
        +LocalDate dataAgendamento
        +Set~Usuario~ convidados
    }

    class Usuario {
        +Integer id
        +String nome
        +String username
        -String senha
        +Set~Evento~ eventos
        +List~String~ perfis
    }

    class EventoCreateDTO {
        +String nome
        +String localizacao
        +LocalDate dataAgendamento
    }

    class EventoDTO {
        +String nome
        +String localizacao
        +LocalDate dataAgendamento
    }

    class UsuarioCreateDTO {
        +String nome
        +String username
        +String senha
    }

    class UsuarioDTO {
        +Integer id
        +String nome
        +String username
    }

    class UsuarioUpdateDTO {
        +String nome
        +String username
        +String senha
    }

    Evento "N" <--> "N" Usuario : convidados / eventos
    EventoCreateDTO ..> Evento : mapeia para
    Evento ..> EventoDTO : mapeia para
    UsuarioCreateDTO ..> Usuario : mapeia para
    Usuario ..> UsuarioDTO : mapeia para
```

---

### Fluxo de Requisição — Criar Evento

```mermaid
sequenceDiagram
    actor Cliente
    participant Security as Spring Security
    participant Controller as EventoController
    participant Service as EventoService
    participant DB as PostgreSQL

    Cliente->>Security: POST /eventos (Basic Auth)
    Security->>Security: Valida credenciais via SecurityDatabaseService
    alt Credenciais inválidas
        Security-->>Cliente: 401 Unauthorized
    else Credenciais válidas
        Security->>Controller: Passa requisição autenticada
        Controller->>Service: criarEvento(EventoCreateDTO)
        Service->>Service: Valida campos obrigatórios
        Service->>Service: Verifica se dataAgendamento é futura
        alt Dados inválidos
            Service-->>Controller: EventoDadosInvalidosException
            Controller-->>Cliente: 400 Bad Request
        else Dados válidos
            Service->>DB: repository.save(evento)
            DB-->>Service: Evento salvo com ID gerado
            Service-->>Controller: EventoDTO
            Controller-->>Cliente: 201 Created + EventoDTO
        end
    end
```

---

### Fluxo de Autenticação — Basic Auth

```mermaid
sequenceDiagram
    actor Cliente
    participant Filter as BasicAuthenticationFilter
    participant SDS as SecurityDatabaseService
    participant DB as PostgreSQL
    participant Encoder as BCryptPasswordEncoder

    Cliente->>Filter: Requisição com header Authorization: Basic <token>
    Filter->>Filter: Decodifica Base64 → username:senha
    Filter->>SDS: loadUserByUsername(username)
    SDS->>DB: findByUsername(username)
    DB-->>SDS: Usuario (com senha hash e perfis)
    SDS-->>Filter: UserDetails (username, senha hash, authorities)
    Filter->>Encoder: matches(senhaInformada, senhaHash)
    alt Senha incorreta
        Filter-->>Cliente: 401 Unauthorized
    else Senha correta
        Filter->>Filter: Seta SecurityContext com autenticação
        Filter-->>Cliente: Requisição prossegue normalmente
    end
```

---

### Fluxo de Autenticação — JWT

```mermaid
sequenceDiagram
    actor Cliente
    participant Auth as AuthController
    participant Manager as AuthenticationManager
    participant JwtService as JwtService
    participant JwtFilter as JwtAuthFilter
    participant SDS as SecurityDatabaseService

    Note over Cliente, JwtService: Passo 1 — Obter o token
    Cliente->>Auth: POST /auth/login (username + senha)
    Auth->>Manager: authenticate(username, senha)
    Manager->>SDS: loadUserByUsername(username)
    SDS-->>Manager: UserDetails
    Manager-->>Auth: Authentication válida
    Auth->>JwtService: gerarToken(userDetails)
    JwtService-->>Auth: token JWT assinado
    Auth-->>Cliente: 200 OK + { token, tipo, expiracao }

    Note over Cliente, SDS: Passo 2 — Usar o token
    Cliente->>JwtFilter: GET /eventos (Authorization: Bearer <token>)
    JwtFilter->>JwtService: extrairUsername(token)
    JwtService-->>JwtFilter: username
    JwtFilter->>SDS: loadUserByUsername(username)
    SDS-->>JwtFilter: UserDetails
    JwtFilter->>JwtService: isTokenValido(token, userDetails)
    alt Token inválido ou expirado
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>JwtFilter: Seta SecurityContext
        JwtFilter-->>Cliente: Requisição prossegue normalmente
    end
```

---

### Fluxo de Adição de Convidado

```mermaid
flowchart TD
    A([POST /eventos/convidados/id?nomeUsuario=xyz]) --> B{Usuário existe?}
    B -- Não --> C[Lança UsuarioNaoEncontradoException]
    C --> D([404 Not Found])
    B -- Sim --> E{Evento existe?}
    E -- Não --> F[Lança EventoNaoEncontradoException]
    F --> G([404 Not Found])
    E -- Sim --> H[Adiciona usuário à lista de convidados]
    H --> I[repository.save]
    I --> J([200 OK — Convidado adicionado com sucesso!])
```

---

## 🗂 Estrutura do Projeto

```
api-eventos/
├── src/
│   ├── main/
│   │   ├── java/com/my/events/
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # Endpoint de login JWT
│   │   │   │   ├── EventoController.java       # Endpoints de eventos
│   │   │   │   └── UsuarioController.java      # Endpoints de usuários
│   │   │   ├── DTO/
│   │   │   │   ├── EventoCreateDTO.java
│   │   │   │   ├── EventoDTO.java
│   │   │   │   ├── EventoUpdateDTO.java
│   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   ├── LoginResponseDTO.java
│   │   │   │   ├── UsuarioCreateDTO.java
│   │   │   │   ├── UsuarioDTO.java
│   │   │   │   └── UsuarioUpdateDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── EventoDadosInvalidosException.java
│   │   │   │   ├── EventoNaoEncontradoException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── UsuarioDadosInvalidosException.java
│   │   │   │   └── UsuarioNaoEncontradoException.java
│   │   │   ├── model/
│   │   │   │   ├── Evento.java                 # Entidade JPA
│   │   │   │   └── Usuario.java                # Entidade JPA
│   │   │   ├── repository/
│   │   │   │   ├── EventoRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtAuthFilter.java          # Filtro de validação JWT
│   │   │   │   ├── JwtService.java             # Geração e validação de tokens
│   │   │   │   ├── SecurityDatabaseService.java # UserDetailsService
│   │   │   │   └── WebSecurityConfig.java      # SecurityFilterChain
│   │   │   ├── service/
│   │   │   │   ├── EventoService.java
│   │   │   │   └── UsuarioService.java
│   │   │   └── EventsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/my/events/
│           ├── config/
│           │   └── TestSecurityConfig.java     # Config de segurança para testes
│           ├── controller/
│           │   ├── EventoControllerTest.java
│           │   └── UsuarioControllerTest.java
│           └── service/
│               ├── EventoServiceTest.java
│               └── UsuarioServiceTest.java
├── src/test/resources/
│   └── application.properties                  # Propriedades para testes
├── .gitignore
├── mvnw
├── mvnw.cmd
└── pom.xml
```

---

## 🗺 Roadmap

- [x] CRUD de eventos
- [x] CRUD de usuários
- [x] Gerenciamento de convidados por evento
- [x] Autenticação via Basic Auth
- [x] Autenticação via JWT (Bearer Token)
- [x] Dois métodos de autenticação coexistindo (Basic Auth + JWT)
- [x] Controle de acesso por perfis (`ROLE_USER`, `ROLE_MANAGERS`)
- [x] Documentação automática com Swagger UI
- [x] Testes unitários na camada de service
- [x] Testes de integração dos controllers com MockMvc
- [x] Tratamento centralizado de exceções (`GlobalExceptionHandler`)
- [x] Validação de dados com Jakarta Validation nas DTOs
- [ ] Paginação nos endpoints de listagem
- [ ] Variáveis de ambiente para configuração sensível
- [ ] Containerização com Docker e Docker Compose
