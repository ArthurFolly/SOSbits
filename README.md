# 🆘 SOSbits - Help Desk

Sistema de **Help Desk / Service Desk** desenvolvido em **Java com Spring Boot** para registro, acompanhamento e gerenciamento de chamados de suporte técnico dentro de uma organização.

O sistema permite que usuários abram chamados, acompanhem o andamento, avaliem atendimentos e que equipes de suporte administrem as solicitações de forma organizada.

---

# 📌 Sobre o Projeto

O **SOSbits Help Desk** foi desenvolvido com o objetivo de centralizar o controle de suporte técnico dentro de uma organização.

A aplicação permite:

- Abertura e gerenciamento de chamados
- Controle de usuários e permissões
- Avaliação do atendimento
- Visualização de relatórios gerenciais
- Monitoramento através de dashboard

---

# 🚀 Tecnologias Utilizadas

| Tecnologia | Descrição |
|------------|-----------|
| Java | Linguagem principal |
| Spring Boot | Framework backend |
| Spring Security | Autenticação e controle de acesso |
| Thymeleaf | Template engine para frontend |
| PostgreSQL | Banco de dados |
| HTML5 | Estrutura das páginas |
| CSS3 | Estilização |
| JavaScript | Interatividade |
| Maven | Gerenciamento de dependências |
| Lombok | Redução de código boilerplate |

---

# 🏗 Arquitetura do Sistema

O sistema segue o padrão **MVC (Model - View - Controller)**.

Camadas da aplicação:

- **Controller** → Recebe requisições HTTP
- **Service** → Regras de negócio
- **Repository** → Acesso ao banco de dados
- **Model** → Representação das entidades
- **Config** → Configurações do sistema e segurança

---

# 👥 Perfis de Usuário

O sistema possui **3 níveis de acesso**.

## 👑 ADMIN

Acesso total ao sistema.

Permissões:

- Gerenciar usuários
- Gerenciar categorias
- Gerenciar setores
- Visualizar todos os chamados
- Visualizar relatórios completos
- Visualizar logs de acesso
- Acessar dashboard administrativo

---

## 🛠 SUPORTE

Responsável por atender os chamados.

Permissões:

- Visualizar todos os chamados
- Alterar status dos chamados
- Atender chamados
- Encerrar chamados
- Visualizar relatórios

---

## 👤 USUÁRIO

Usuário final que abre chamados.

Permissões:

- Criar chamados
- Acompanhar seus chamados
- Avaliar atendimento
- Visualizar histórico de chamados

---

# 🎫 Gestão de Chamados

O sistema permite:

- Criar chamados
- Editar chamados
- Classificar por categoria
- Definir prioridade
- Acompanhar status
- Registrar data de abertura
- Registrar data de fechamento

Status disponíveis:

- ABERTO
- EM_ANDAMENTO
- FECHADO

---

# ⭐ Avaliação de Atendimento

Após o chamado ser **fechado**, o usuário pode avaliar o atendimento.

Escala de avaliação:

⭐ 1 estrela  
⭐⭐ 2 estrelas  
⭐⭐⭐ 3 estrelas  
⭐⭐⭐⭐ 4 estrelas  
⭐⭐⭐⭐⭐ 5 estrelas

Essas avaliações alimentam os relatórios estatísticos do sistema.

---

# 📊 Relatórios

O sistema possui relatórios administrativos.

### 📈 Relatório Geral de Chamados

Lista todos os chamados com filtros.

### 📊 Relatório por Status

Gráfico mostrando:

- Chamados abertos
- Chamados em andamento
- Chamados fechados

### 📊 Relatório por Prioridade

Distribuição de chamados por prioridade.

### ⭐ Relatório de Avaliações

Mostra:

- Quantidade de estrelas
- Percentual de satisfação

---

# 📊 Dashboard

O dashboard apresenta indicadores importantes do sistema:

- Total de chamados
- Chamados abertos
- Chamados em andamento
- Chamados fechados
- Chamados recentes

---

# 🔐 Segurança

O sistema utiliza **Spring Security**.

Recursos implementados:

- Login seguro
- Controle de acesso por perfil
- Senhas criptografadas com **BCrypt**
- Proteção de rotas
- Sessão autenticada

---

# 🗄 Estrutura do Banco de Dados

Principais tabelas do sistema.

| Tabela | Descrição |
|------|------|
| usuario | Usuários do sistema |
| perfil | Perfis de acesso |
| chamado | Chamados de suporte |
| categoria | Tipos de ocorrência |
| setor | Setores da empresa |
| avaliacao | Avaliação de atendimento |
| log_acessos | Registro de login e logout |

---

# 👨‍💻 Autor

Desenvolvido por

**Arthur Folly Nagib Ferreira**

# ⚙ Como Executar o Projeto

### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/seu-usuario/sosbits.git

