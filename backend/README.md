# TreinaMais - Backend API

API REST construída com **Java 21**, **Spring Boot 3.3**, **Spring Security 6**, **Spring Data JPA** e autenticação via **JWT (JSON Web Token)**.

---

## 📁 Estrutura de Pastas

```
backend/
├── pom.xml                                  # Gerenciador de dependências Maven
├── mvnw / mvnw.cmd                          # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/com/pucgo/edu/treinamais/
│   │   │   ├── TreinaMaisBackendApplication.java  # Classe principal (Spring Boot)
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java          # Configuração de CORS (Android/Web)
│   │   │   │   └── SecurityConfig.java      # Configuração do Spring Security 6 & rotas
│   │   │   ├── controller/                  # Controladores REST (@RestController)
│   │   │   │   └── AuthController.java      # Endpoints de Login, Cadastro e Perfil
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   ├── request/                 # Requisições (LoginRequest, RegisterRequest)
│   │   │   │   └── response/                # Respostas (AuthResponse, MessageResponse)
│   │   │   ├── exception/                   # Tratamento global de erros
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/                       # Entidades JPA (@Entity)
│   │   │   │   ├── TipoUsuario.java         # Enum (PROFESSOR, ALUNO, ADMIN)
│   │   │   │   ├── StatusUsuario.java       # Enum (ATIVO, INATIVO, BLOQUEADO)
│   │   │   │   ├── Usuario.java             # Tabela: usuarios
│   │   │   │   ├── Professor.java           # Tabela: professores
│   │   │   │   ├── Aluno.java               # Tabela: alunos
│   │   │   │   ├── Sessao.java              # Tabela: sessoes
│   │   │   │   ├── Treino.java              # Tabela: treinos
│   │   │   │   ├── ExercicioTreino.java     # Tabela: exercicios_treino
│   │   │   │   └── AlunoTreino.java         # Tabela: alunos_treino
│   │   │   ├── repository/                  # Repositórios Spring Data JPA
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── ProfessorRepository.java
│   │   │   │   ├── AlunoRepository.java
│   │   │   │   ├── SessaoRepository.java
│   │   │   │   ├── TreinoRepository.java
│   │   │   │   ├── ExercicioTreinoRepository.java
│   │   │   │   └── AlunoTreinoRepository.java
│   │   │   ├── security/                    # Filtros e Provedores JWT
│   │   │   │   ├── JwtAuthenticationFilter.java  # Filtro OncePerRequest para validar token Bearer
│   │   │   │   ├── JwtTokenProvider.java         # Geração e validação de claims JWT
│   │   │   │   └── UserDetailsServiceImpl.java   # Integração com Spring UserDetailsService
│   │   │   └── service/                     # Regras de Negócio (@Service)
│   │   │       └── AuthService.java         # Cadastro de usuários, alunos/professores e login
│   │   └── resources/
│   │       ├── application.properties       # Configuração base e porta (8081)
│   │       ├── application-dev.properties   # Perfil DEV (Banco H2 em memória)
│   │       └── application-prod.properties  # Perfil PROD (Banco PostgreSQL via Docker)
│   └── test/
│       └── java/com/pucgo/edu/treinamais/
│           └── TreinaMaisBackendApplicationTests.java
```

---

## 🗄️ Modelo Relacional de Dados

| Tabela | Descrição | Colunas Principais |
|---|---|---|
| **`usuarios`** | Dados comuns da conta | `id`, `nome`, `email`, `senha_hash`, `tipo`, `status`, `criado_em`, `atualizado_em` |
| **`professores`** | Dados específicos do professor | `id`, `usuario_id` (FK), `cref`, `telefone`, `criado_em` |
| **`alunos`** | Dados específicos do aluno | `id`, `usuario_id` (FK), `professor_id` (FK), `cpf`, `data_nascimento`, `telefone`, `criado_em` |
| **`sessoes`** | Controle de autenticação | `id`, `usuario_id` (FK), `token_hash`, `expira_em`, `revogado_em`, `criado_em` |
| **`treinos`** | Fichas de treino | `id`, `professor_id` (FK), `nome`, `descricao`, `objetivo`, `status`, `criado_em` |
| **`exercicios_treino`** | Exercícios que compõem o treino | `id`, `treino_id` (FK), `nome_exercicio`, `series`, `repeticoes`, `carga`, `descanso_segundos`, `ordem`, `observacoes` |
| **`alunos_treino`** | Associação entre Aluno e Treino | `id`, `aluno_id` (FK), `treino_id` (FK), `data_inicio`, `data_fim`, `ativo` |

---

## 🚀 Como Executar

### 1. Iniciar o Banco PostgreSQL no Docker
Na raiz do projeto:
```bash
cd infra
docker compose up -d
```
O PostgreSQL estará disponível em `localhost:5432` e o pgAdmin em `http://localhost:5050`.

### 2. Rodar o Backend conectado ao PostgreSQL (Docker)
No diretório `backend`:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### 3. Rodar em Modo de Teste Rápido (H2 em memória)
Se preferir rodar sem abrir o Docker:
```bash
./mvnw spring-boot:run
```
*(ou `mvn spring-boot:run`)*

---

## 🔒 Endpoints Disponíveis

| Método | Endpoint | Protegido? | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/register` | Não | Cadastra um novo usuário (`ALUNO`, `PROFESSOR` ou `ADMIN`), persistindo os dados específicos |
| `POST` | `/api/auth/login` | Não | Autentica, registra na tabela `sessoes` e retorna o token JWT |
| `GET` | `/api/auth/me` | Sim (`Bearer <token>`) | Retorna os dados do usuário autenticado |
