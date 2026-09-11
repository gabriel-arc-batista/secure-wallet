# Secure Wallet

Aplicação de carteira digital para cadastro de usuários, autenticação, consulta de saldo, depósitos simulados, transferências e extrato de movimentações.

O projeto foi desenvolvido como uma aplicação full stack. O backend concentra as regras financeiras e a segurança. O frontend oferece uma interface simples para utilizar os recursos da API.

## Funcionalidades

- Cadastro de usuário com validação de dados
- Senha armazenada com BCrypt
- Login com token JWT
- Carteira criada automaticamente no cadastro
- Consulta de saldo
- Depósito simulado
- Transferência entre usuários
- Extrato paginado
- Validação de saldo e valores
- Controle de atualização concorrente da carteira
- Tratamento padronizado de erros

## Tecnologias

### Backend

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Maven
- JUnit e Mockito

### Frontend

- React
- Vite
- HTML e CSS

### Infraestrutura

- Docker
- Docker Compose
- GitHub Actions

## Organização do backend

O backend utiliza um monólito organizado por domínio.

```text
src/main/java/com/gabrielarcanjo/securewallet
├── auth         autenticação e geração de token
├── common       respostas de erro da API
├── config       configuração de senha, CORS e segurança
├── transaction  transferências e extrato
├── user         cadastro e persistência de usuários
└── wallet       saldo e depósitos
```

Os controllers recebem as requisições HTTP. Os services executam as regras da aplicação. Os repositories fazem o acesso ao PostgreSQL por meio do Spring Data JPA.

## Modelo de dados

O banco possui três tabelas principais:

- `users`: dados do usuário e hash da senha
- `wallets`: saldo, proprietário e versão para controle de concorrência
- `wallet_transactions`: depósitos e movimentações de entrada e saída

Uma transferência grava duas movimentações. A carteira do remetente recebe `TRANSFER_OUT` e a carteira do destinatário recebe `TRANSFER_IN`. As duas operações são executadas na mesma transação do banco.

## Regras principais

- Cada usuário possui uma única carteira.
- O e-mail é único e armazenado em letras minúsculas.
- Depósitos e transferências devem ter valor maior que zero.
- Uma transferência exige saldo suficiente.
- O usuário não pode transferir para a própria carteira.
- Alterações de saldo usam controle de versão para detectar atualizações concorrentes.
- A senha e seu hash nunca aparecem nas respostas da API.

## Endpoints

| Método | Endpoint | Autenticação | Descrição |
| --- | --- | --- | --- |
| `POST` | `/api/users` | Não | Cadastra um usuário |
| `POST` | `/api/auth/login` | Não | Autentica e retorna um JWT |
| `GET` | `/api/wallet` | Sim | Consulta a carteira do usuário autenticado |
| `POST` | `/api/wallet/deposits` | Sim | Adiciona saldo simulado |
| `POST` | `/api/transfers` | Sim | Transfere saldo para outro usuário |
| `GET` | `/api/transactions` | Sim | Lista o extrato paginado |

Os endpoints protegidos esperam o token no header:

```http
Authorization: Bearer TOKEN
```

## Executando com Docker

### Requisitos

- Docker
- Docker Compose

Defina duas variáveis no terminal antes de iniciar os containers:

- `DB_PASSWORD`: senha local do PostgreSQL
- `JWT_SECRET`: chave Base64 com pelo menos 32 bytes

Uma chave pode ser gerada com:

```bash
openssl rand -base64 32
```

Exemplo no PowerShell usando valores definidos por você:

```powershell
$env:DB_PASSWORD="SUA_SENHA_LOCAL"
$env:JWT_SECRET="SUA_CHAVE_BASE64"
docker compose up --build
```

Depois da inicialização:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- PostgreSQL: disponível apenas para os serviços do Docker Compose

As migrations são executadas automaticamente pelo Flyway quando o backend inicia.

## Executando sem Docker

### Backend

Requisitos:

- Java 25
- PostgreSQL

Crie o banco `secure_wallet`, configure o usuário da aplicação e defina as variáveis `DB_PASSWORD` e `JWT_SECRET`. Em seguida, execute:

```bash
./mvnw spring-boot:run
```

### Frontend

Requisitos:

- Node.js 22 ou superior

```bash
cd frontend
npm install
npm run dev
```

O Vite inicia o frontend em `http://localhost:5173`.

## Testes

Os testes unitários cobrem:

- criação de usuário e carteira
- bloqueio de e-mail duplicado
- autenticação com credenciais válidas e inválidas
- depósito e validação de valor
- transferência entre carteiras
- saldo insuficiente
- transferência para a própria carteira

Para executar os testes do backend:

```bash
./mvnw test
```

Para gerar o build do frontend:

```bash
cd frontend
npm install
npm run build
```

O GitHub Actions executa os testes do backend e o build do frontend a cada push e pull request na branch `main`.

## Segurança

- O repositório não contém senhas, tokens ou arquivos `.env`.
- A senha do banco e a chave JWT são fornecidas por variáveis de ambiente.
- As senhas dos usuários são protegidas com BCrypt.
- A API utiliza sessão stateless e valida o JWT nos endpoints privados.
- O CORS aceita apenas a origem configurada para o frontend.
- Erros internos não expõem stack trace nas respostas.
- O container do backend executa a aplicação com um usuário sem privilégios administrativos.

## Limitações atuais

- O depósito é uma simulação e não possui integração bancária.
- A aplicação trabalha apenas com real brasileiro.
- O projeto não possui recuperação de senha.
- O extrato mostra as vinte movimentações mais recentes no frontend.
- O token permanece válido até expirar ou até o usuário encerrar a sessão no navegador.
