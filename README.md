# Meu Postinho - Sistema de Otimização de Atendimento em UBS

Plataforma inovadora para otimizar o atendimento em Unidades Básicas de Saúde (UBS) do SUS, reduzindo no-shows, idas frustradas e melhorando a adesão ao tratamento.

---

## Visão Geral

**Problema a resolver:**
- Redução de no-shows (faltas) em consultas agendadas
- Idas frustradas ao posto (medicamento em falta)
- Falhas de comunicação entre UBS e população

## Recursos Principais

### Para Moradores (ROLE_MORADOR)
- Cadastro e autenticação com CPF
- Visualizar medicamentos disponíveis
- Solicitar medicamentos
- Agendar consultas
- Cancelar agendamentos
- Receber notícias e comunicados da UBS

### Para Agentes (ACS/ACE) (ROLE_AGENTE)
- Gerenciar estoque de medicamentos
- Aceitar/recusar solicitações de medicamentos
- Criar vagas/horários de consulta
- Validação CPF + CNS via CNES
- Publicar notícias/comunicados


## Stack Tecnológico

```
Backend:
├── Java 21
├── Spring Boot 3.x
├── Spring Data JPA
├── Spring Security + JWT
├── PostgreSQL 15
├── Flyway (Migrations)
├── Springdoc OpenAPI/Swagger
└── Maven

Infrastructure:
├── Docker
├── Docker Compose
└── Multi-stage Build
```

---

## Como Configurar e Rodar

### Pré-requisitos
- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 15 (ou usar Docker)

### Opção 1: Rodando com Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/seu-repo/meu-postinho.git
cd meu-postinho

# Suba os containers
docker-compose up --build

# Aguarde ~30 segundos para as migrações rodarem
# Acesse: http://localhost:8080
```

### Opção 2: Rodando Localmente

```bash
# Configure PostgreSQL
createdb meupostinho -U postgres

# Compile o projeto
mvn clean install

# Rode a aplicação
mvn spring-boot:run

# Acesse: http://localhost:8080
```

---

## Documentação da API

### Swagger/OpenAPI
Acesse em: `http://localhost:8080/swagger-ui.html`

### Endpoints Principais

#### Autenticação
```
POST /api/auth/login
POST /api/auth/register
```

#### Usuários e Agentes
```
POST   /api/usuarios              # Registrar morador
POST   /api/agentes                # Registrar agente (com validação CNES)
GET    /api/agentes/{id}           # Obter agente
```

#### UBS
```
GET    /api/ubs                    # Listar todas
GET    /api/ubs/{id}               # Obter por ID
```

#### Medicamentos
```
GET    /api/medicamentos/disponiveis   # Listar disponíveis
GET    /api/medicamentos                # Listar todos
GET    /api/medicamentos/{id}           # Obter por ID
POST   /api/medicamentos                # Criar (Admin/Agente)
```

#### Estoque
```
POST   /api/estoques               # Criar estoque (Agente)
GET    /api/estoques/ubs/{ubsId}   # Listar por UBS (Agente)
PUT    /api/estoques/{id}          # Atualizar (Agente)
DELETE /api/estoques/{id}          # Deletar (Agente)
```

#### Solicitações de Medicamentos
```
POST   /api/solicitacoes                 # Criar solicitação (Morador)
GET    /api/solicitacoes/{id}            # Obter por ID
GET    /api/solicitacoes/usuario/{id}    # Listar minhas solicitações
GET    /api/solicitacoes/ubs/{id}/pendentes  # Pendentes (Agente)
PUT    /api/solicitacoes/{id}/aceitar    # Aceitar (Agente)
PUT    /api/solicitacoes/{id}/recusar    # Recusar (Agente)
```

#### Vagas e Agendamentos
```
POST   /api/vagas                  # Criar vaga (Agente)
GET    /api/vagas/ubs/{ubsId}      # Listar por UBS (Agente)
GET    /api/vagas/disponiveis      # Filtrar disponíveis
POST   /api/agendamentos           # Agendar consulta (Morador)
GET    /api/agendamentos/meus      # Meus agendamentos (Morador)
DELETE /api/agendamentos/{id}      # Cancelar (Morador)
PUT    /api/agendamentos/{id}/comparecimento        # Marcar presença (Agente)
PUT    /api/agendamentos/{id}/nao-comparecimento    # Marcar falta (Agente)
```

#### Notícias
```
POST   /api/noticias               # Criar notícia (Agente)
GET    /api/noticias/{id}          # Obter por ID
GET    /api/noticias/minhas        # Notícias da minha UBS (Morador)
GET    /api/noticias/ubs/{ubsId}   # Notícias de uma UBS
PUT    /api/noticias/{id}          # Editar (Agente)
DELETE /api/noticias/{id}          # Deletar (Agente)
```

---

## Autenticação e Autorização

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@teste.com",
    "senha": "senha123456"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "cpf": "12345678901",
  "nome": "João Silva",
  "email": "usuario@teste.com",
  "role": "ROLE_MORADOR",
  "ubsId": 1
}
```

### Usando o Token
```bash
curl -X GET http://localhost:8080/api/agendamentos/meus \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Modelo de Dados

### Entidades Principais

```
Usuario (herança SINGLE_TABLE)
├── Morador (ROLE_MORADOR)
└── Agente (ROLE_AGENTE)
    ├── CPF (validado)
    ├── CNS (validado via CNES)
    ├── Tipo (ACS/ACE)
    └── Status CNES (VERIFICADO/PENDENTE/REJEITADO)

UBS
├── Usuários (N:1)
├── Agentes (N:1)
├── Medicamentos em Estoque (N:N via EstoqueMedicamento)
├── Vagas (1:N)
└── Notícias (1:N)

Medicamento
├── Estoque por UBS (N:N)
└── Solicitações (1:N)

Vaga
└── Agendamentos (1:N)

Agendamento
└── Status (CONFIRMADO/COMPARECEU/NAO_COMPARECEU/CANCELADO)

SolicitacaoMedicamento
└── Status (PENDENTE/ACEITA/RECUSADA/ENTREGUE)
```

---

## Estrutura do Banco de Dados

### Tabelas principais:
- `ubs` - Unidades de Saúde
- `usuario` - Usuários (Morador + Agente)
- `usuario_roles` - Papéis de usuário
- `medicamento` - Catálogo de medicamentos
- `estoque_medicamento` - Estoque por UBS
- `solicitacao_medicamento` - Pedidos de medicamentos
- `vaga` - Horários de consulta
- `agendamento` - Consultas marcadas
- `noticia` - Comunicados das UBS

### Migrações Flyway:
- `V1__Initial_Schema.sql` - Criação das tabelas
- `V2__Insert_Seed_Data.sql` - Dados iniciais (5 UBS + 25 medicamentos)

---

## Testando com cURL

### 1. Registrar um Morador

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "nome": "João Silva",
    "telefone": "(11) 98765-4321",
    "email": "joao@teste.com",
    "senha": "Senha@123456",
    "dataNascimento": "1990-01-15",
    "endereco": "Rua A, 123",
    "cep": "01310-100",
    "ubsId": 1
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@teste.com",
    "senha": "Senha@123456"
  }'
```

### 3. Listar UBS

```bash
curl -X GET http://localhost:8080/api/ubs
```

### 4. Listar Medicamentos Disponíveis

```bash
curl -X GET http://localhost:8080/api/medicamentos/disponiveis \
  -H "Authorization: Bearer {SEU_TOKEN}"
```

### 5. Solicitar Medicamento (Morador)

```bash
curl -X POST http://localhost:8080/api/solicitacoes \
  -H "Authorization: Bearer {SEU_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "medicamentoId": 1,
    "ubsId": 1,
    "quantidade": 2
  }'
```

### 6. Aceitar Solicitação (Agente)

```bash
curl -X PUT http://localhost:8080/api/solicitacoes/1/aceitar \
  -H "Authorization: Bearer {SEU_TOKEN_AGENTE}"
```

### 7. Criar Vaga (Agente)

```bash
curl -X POST http://localhost:8080/api/vagas \
  -H "Authorization: Bearer {SEU_TOKEN_AGENTE}" \
  -H "Content-Type: application/json" \
  -d '{
    "ubsId": 1,
    "data": "2025-02-15",
    "horaInicio": "09:00:00",
    "horaFim": "09:30:00",
    "especialidade": "Clínica Geral",
    "profissional": "Dr. Silva"
  }'
```

### 8. Agendar Consulta (Morador)

```bash
curl -X POST "http://localhost:8080/api/agendamentos?usuarioId=1&vagaId=1" \
  -H "Authorization: Bearer {SEU_TOKEN}"
```

---

## Estrutura do Projeto

```
meu-postinho/
├── src/main/java/br/com/fiap/postech/meu_postinho/
│   ├── controller/              # REST Controllers
│   │   ├── AuthController.java
│   │   ├── UBSController.java
│   │   ├── AgenteController.java
│   │   ├── MedicamentoController.java
│   │   ├── EstoqueMedicamentoController.java
│   │   ├── SolicitacaoMedicamentoController.java
│   │   ├── VagaController.java
│   │   ├── AgendamentoController.java
│   │   └── NoticiaController.java
│   ├── service/                 # Lógica de Negócio
│   │   ├── AuthService.java
│   │   ├── UBSService.java
│   │   ├── AgenteService.java
│   │   ├── MedicamentoService.java
│   │   ├── EstoqueMedicamentoService.java
│   │   ├── SolicitacaoMedicamentoService.java
│   │   ├── VagaService.java
│   │   ├── AgendamentoService.java
│   │   └── NoticiaService.java
│   ├── repository/              # Acesso a Dados
│   │   ├── UsuarioRepository.java
│   │   ├── AgenteRepository.java
│   │   ├── UBSRepository.java
│   │   ├── MedicamentoRepository.java
│   │   ├── EstoqueMedicamentoRepository.java
│   │   ├── SolicitacaoMedicamentoRepository.java
│   │   ├── VagaRepository.java
│   │   ├── AgendamentoRepository.java
│   │   └── NoticiaRepository.java
│   ├── domain/                  # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Agente.java
│   │   ├── UBS.java
│   │   ├── Medicamento.java
│   │   ├── EstoqueMedicamento.java
│   │   ├── SolicitacaoMedicamento.java
│   │   ├── Vaga.java
│   │   ├── Agendamento.java
│   │   └── Noticia.java
│   ├── dto/                     # DTOs (Request/Response)
│   │   ├── UsuarioDTO.java
│   │   ├── AgenteDTO.java
│   │   ├── UBSDTO.java
│   │   ├── MedicamentoDTO.java
│   │   ├── EstoqueMedicamentoDTO.java
│   │   ├── SolicitacaoMedicamentoDTO.java
│   │   ├── VagaDTO.java
│   │   ├── AgendamentoDTO.java
│   │   ├── NoticiaDTO.java
│   │   ├── LoginDTO.java
│   │   └── AuthResponse.java
│   ├── config/                  # Configurações
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── CustomUserDetailsService.java
│   │   └── OpenAPIConfig.java
│   ├── exception/               # Tratamento de Erros
│   │   ├── ResourceNotFoundException.java
│   │   ├── BadRequestException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   ├── util/                    # Utilitários
│   │   ├── CPFValidator.java
│   │   └── PhoneValidator.java
│   ├── integration/             # Integrações Externas
│   │   └── CnesMockService.java
│   └── MeuPostinhoApplication.java
├── src/main/resources/
│   ├── application.properties   # Configurações
│   └── db/migration/
│       ├── V1__Initial_Schema.sql
│       └── V2__Insert_Seed_Data.sql
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Variáveis de Ambiente

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/meupostinho
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
APP_JWT_SECRET=ChaveSecreta...
APP_JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

---

## Checklist de Validação

- [x] Projeto compila com `mvn clean install`
- [x] Docker Compose sobe com `docker-compose up`
- [x] Swagger acessível em http://localhost:8080/swagger-ui.html
- [x] Todos os endpoints RF001-RF014 implementados
- [x] JWT funcionando (login retorna token válido)
- [x] Validações de CPF, telefone, estoque
- [x] Territorialização (morador vê sua UBS)
- [x] Seeds de medicamentos RENAME carregados
- [x] Tratamento de exceções (GlobalExceptionHandler)
- [x] README completo

---

## Dados de Teste Iniciais

### UBS Criadas (via Seed)
1. **UBS Parque São Vicente** - Código CNES: 3509502
2. **UBS Vila Mariana** - Código CNES: 3509503
3. **UBS Centro** - Código CNES: 3509504
4. **UBS Zona Leste** - Código CNES: 3509505
5. **UBS Zona Norte** - Código CNES: 3509506

### Medicamentos (Lista RENAME)
25 medicamentos comuns no SUS: Paracetamol, Ibuprofeno, Losartana, Metformina, Amoxicilina, Azitromicina, Omeprazol, Ranitidina, e outros.


