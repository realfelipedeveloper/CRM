CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(160) NOT NULL,
  email VARCHAR(200) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  perfil VARCHAR(40) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE clientes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(160) NOT NULL,
  email VARCHAR(200),
  telefone VARCHAR(30),
  documento VARCHAR(30),
  tags VARCHAR(500),
  status VARCHAR(30) NOT NULL DEFAULT 'ATIVO',
  consentimento_marketing BOOLEAN NOT NULL DEFAULT FALSE,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT uk_clientes_documento UNIQUE (documento)
);

CREATE INDEX idx_clientes_nome ON clientes (LOWER(nome));
CREATE INDEX idx_clientes_status_criado ON clientes (status, criado_em DESC);

CREATE TABLE leads (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(160) NOT NULL,
  email VARCHAR(200),
  telefone VARCHAR(30),
  origem VARCHAR(80) NOT NULL,
  status VARCHAR(30) NOT NULL,
  responsavel VARCHAR(160),
  valor_estimado NUMERIC(15,2) NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_leads_status_criado ON leads (status, criado_em DESC);

CREATE TABLE oportunidades (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  titulo VARCHAR(180) NOT NULL,
  cliente_id UUID REFERENCES clientes(id),
  etapa VARCHAR(30) NOT NULL,
  valor NUMERIC(15,2) NOT NULL,
  probabilidade INTEGER NOT NULL CHECK (probabilidade BETWEEN 0 AND 100),
  responsavel VARCHAR(160) NOT NULL,
  previsao_fechamento DATE,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_oportunidades_etapa ON oportunidades (etapa, atualizado_em DESC);

CREATE TABLE agendamentos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cliente_id UUID NOT NULL REFERENCES clientes(id),
  profissional VARCHAR(160) NOT NULL,
  servico VARCHAR(180) NOT NULL,
  inicio TIMESTAMPTZ NOT NULL,
  fim TIMESTAMPTZ NOT NULL,
  status VARCHAR(30) NOT NULL,
  valor NUMERIC(15,2) NOT NULL DEFAULT 0,
  versao BIGINT NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CHECK (fim > inicio)
);

CREATE INDEX idx_agendamentos_profissional_periodo ON agendamentos (profissional, inicio, fim);
CREATE INDEX idx_agendamentos_status_inicio ON agendamentos (status, inicio);

CREATE TABLE lancamentos_financeiros (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  descricao VARCHAR(200) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  valor NUMERIC(15,2) NOT NULL CHECK (valor >= 0),
  vencimento DATE NOT NULL,
  pagamento DATE,
  categoria VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lancamentos_tipo_status_vencimento ON lancamentos_financeiros (tipo, status, vencimento);

CREATE TABLE auditoria (
  id BIGSERIAL PRIMARY KEY,
  usuario_email VARCHAR(200) NOT NULL,
  acao VARCHAR(80) NOT NULL,
  recurso VARCHAR(80) NOT NULL,
  recurso_id VARCHAR(80),
  ip VARCHAR(64),
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auditoria_recurso_data ON auditoria (recurso, criado_em DESC);

