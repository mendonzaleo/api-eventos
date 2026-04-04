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
spring.datasource.username= #usuário do banco dedados aqui
spring.datasource.password= #senha do banco de dados aqui

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Pool de conexões
spring.datasource.hikari.maximum-pool-size=5

# Logging
logging.level.root=INFO
logging.level.org.springframework=DEBUG
logging.level.com.meus.eventos=TRACE
```


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

No Swagger UI você pode visualizar todos os endpoints, seus parâmetros, modelos de requisição e resposta, e executar chamadas diretamente pelo navegador utilizando autenticação Basic Auth.

---

## 🔗 Endpoints

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

#### Exemplo — Criar evento

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

A API utiliza **HTTP Basic Authentication**.

Todas as requisições (exceto `POST /login`) exigem o cabeçalho:

```http
Authorization: Basic <base64(username:senha)>
```

Exemplo com `curl`:

```bash
curl -u lmendonza:minhasenha123 http://localhost:9090/eventos
```

### Futuro: Migração para JWT

A autenticação por **JWT (JSON Web Token)** está planejada para uma versão futura. A migração consistirá em:

1. Adicionar a dependência `spring-boot-starter-oauth2-resource-server`
2. Criar um endpoint `POST /login` que retorna o token
3. Substituir `.httpBasic()` por validação de Bearer token no `SecurityFilterChain`
4. Atualizar os testes para usar `.with(jwt())` do `spring-security-test`

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
│   │   │   │   ├── EventoController.java      # Endpoints de eventos
│   │   │   │   └── UsuarioController.java     # Endpoints de usuários
│   │   │   ├── DTO/
│   │   │   │   ├── EventoCreateDTO.java
│   │   │   │   ├── EventoDTO.java
│   │   │   │   ├── EventoUpdateDTO.java
│   │   │   │   ├── UsuarioCreateDTO.java
│   │   │   │   ├── UsuarioDTO.java
│   │   │   │   └── UsuarioUpdateDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── EventoDadosInvalidosException.java
│   │   │   │   ├── EventoNaoEncontradoException.java
│   │   │   │   └── UsuarioNaoEncontradoException.java
│   │   │   ├── model/
│   │   │   │   ├── Evento.java                # Entidade JPA
│   │   │   │   └── Usuario.java               # Entidade JPA
│   │   │   ├── repository/
│   │   │   │   ├── EventoRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityDatabaseService.java  # UserDetailsService
│   │   │   │   └── WebSecurityConfig.java         # SecurityFilterChain
│   │   │   ├── service/
│   │   │   │   ├── EventoService.java
│   │   │   │   └── UsuarioService.java
│   │   │   └── EventsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/my/events/
│           └── service/
│               ├── EventoServiceTest.java
│               └── UsuarioServiceTest.java
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
- [x] Controle de acesso por perfis (`ROLE_USER`, `ROLE_MANAGERS`)
- [x] Documentação automática com Swagger UI
- [x] Testes unitários na camada de service
- [ ] Migração para autenticação via JWT
- [ ] Testes de integração com MockMvc
- [ ] Paginação nos endpoints de listagem
- [ ] Variáveis de ambiente para configuração sensível
- [ ] Containerização com Docker e Docker Compose
