# GETTER — Plataforma de Gestão e Levantamento de Requisitos

O **GETTER** é uma aplicação web concebida para simplificar e organizar o processo de engenharia de software, facilitando o levantamento, especificação e gestão colaborativa de requisitos de projetos de software.

Projeto desenvolvido no âmbito do Projeto Final da Licenciatura em Engenharia Informática.

---

## 🛠️ Stack

### Backend
- **Java 17+** & **Spring Boot**
- **Spring Security** com autenticação baseada em tokens **JWT**
- **Spring Data JPA** / **Hibernate**
- **PostgreSQL** (alojado via Neon)
- **Maven** (gestão de dependências e build)

### Frontend
- **Vue.js 3** (Composition API)
- **Vite** (build tool e servidor de desenvolvimento)
- **Vue Router** (gestão de rotas da aplicação)
- **Tailwind CSS** (estilização e componentes de UI)

---

## ✨ Funcionalidades Principais

- **Autenticação e Permissões:** Registo, início de sessão seguro com JWT e controlo de acesso baseado em papéis (roles).
- **Gestão de Requisitos:** Criação, edição, categorização e rastreio do ciclo de vida dos requisitos.
- **Formulários Interativos:** Questionários e mecanismos estruturados para levantamento ágil de requisitos.
- **Painel de Controlo (Dashboard):** Visualização clara do estado atual e métricas de cada projeto.

---

## 📂 Estrutura do Repositório

```text
├── backend/ (ou raiz com código Java)
│   ├── src/main/java/com/example/app/
│   │   ├── entity/          # Entidades da base de dados (User, FormQuestion, etc.)
│   │   ├── repository/      # Interfaces de acesso a dados (JPA Repositories)
│   │   ├── service/         # Lógica de negócio da aplicação
│   │   ├── controller/      # Endpoints REST expostos
│   │   └── AppApplication.java
│   └── pom.xml
└── frontend/
    ├── src/
    │   ├── components/      # Componentes reutilizáveis (auth, dashboard, etc.)
    │   ├── router/          # Configurações de rotas (Vue Router)
    │   └── assets/          # Ficheiros estáticos e estilos globais
    ├── package.json
    └── vite.config.js
