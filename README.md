# CRM Áurea

CRM modular, single-tenant por instalação, direcionado inicialmente a clínicas de estética. O primeiro corte vertical entrega autenticação, dashboard executivo, clientes, leads, pipeline, agenda e visão financeira, com base para BI, automações e agentes.

## Início rápido

1. Copie `.env.example` para `.env` e troque os segredos.
2. Execute `docker compose up --build`.
3. Acesse `http://localhost:4200`.
4. Entre com `admin@clinica.local` e `Admin@123` (criados exclusivamente pelo perfil `demo` do Compose).

Serviços:

- Frontend Angular: `http://localhost:4200`
- API Spring Boot/OpenAPI: `http://localhost:8080/swagger-ui.html`
- Healthcheck: `http://localhost:8080/actuator/health`
- n8n: `http://localhost:5678`
- Serviço de agentes: `http://localhost:8090/health`

## Desenvolvimento

### Backend

Requer Java 21 e Maven 3.9+. Execute `mvn test` em `backend/`.

### Frontend

Requer Node.js 22+. Execute `npm install` e `npm start` em `frontend/`.

## Segurança e LGPD

O projeto aplica RBAC, JWT de curta duração, trilha de auditoria, separação de dados clínicos e comerciais, paginação server-side e não registra anamneses ou fotografias em logs. Os valores de demonstração não devem ser usados em produção. Conformidade jurídica depende de avaliação do controlador e do encarregado de dados.

## Arquitetura

O backend é um monólito modular Spring Boot. PostgreSQL atende o modelo transacional e agregações iniciais. Angular consome contratos REST. n8n orquestra integrações sem assumir regras de negócio. O serviço de agentes acessa somente ferramentas autorizadas da API.

