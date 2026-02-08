-- V1__Schema.sql
-- Schema consolidado (alinhado às entidades JPA)

CREATE SCHEMA IF NOT EXISTS public;
SET search_path TO public;

CREATE TABLE IF NOT EXISTS ubs (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    codigo_cnes VARCHAR(20) UNIQUE,
    endereco VARCHAR(500) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    dtype VARCHAR(31) NOT NULL DEFAULT 'Usuario',
    cpf VARCHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_nascimento VARCHAR(50),
    endereco VARCHAR(500) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE RESTRICT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    -- campos de Agente (SINGLE_TABLE)
    cns VARCHAR(15) UNIQUE,
    tipo_agente VARCHAR(20),
    status_cnes VARCHAR(50),
    data_verificacao_cnes TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    role VARCHAR(100) NOT NULL,
    PRIMARY KEY (usuario_id, role)
);

CREATE TABLE IF NOT EXISTS medicamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100) NOT NULL,
    posologia TEXT,
    unidade VARCHAR(50) NOT NULL,
    codigo_catmat VARCHAR(50) UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS estoque_medicamento (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE CASCADE,
    quantidade INT NOT NULL,
    quantidade_minima INT NOT NULL DEFAULT 10,
    data_entrada TIMESTAMP,
    data_vencimento TIMESTAMP,
    em_falta BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (ubs_id, medicamento_id)
);

CREATE TABLE IF NOT EXISTS solicitacao_medicamento (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE CASCADE,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    quantidade INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    justificativa_recusa TEXT,
    data_processamento TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vaga (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    data_vaga DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    profissional VARCHAR(255) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agendamento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    vaga_id BIGINT NOT NULL REFERENCES vaga(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    observacoes TEXT,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (vaga_id)
);

CREATE TABLE IF NOT EXISTS noticia (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    data_publicacao TIMESTAMP NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_usuario_dtype ON usuario(dtype);
CREATE INDEX IF NOT EXISTS idx_usuario_ubs ON usuario(ubs_id);
CREATE INDEX IF NOT EXISTS idx_estoque_ubs ON estoque_medicamento(ubs_id);
CREATE INDEX IF NOT EXISTS idx_estoque_medicamento ON estoque_medicamento(medicamento_id);
CREATE INDEX IF NOT EXISTS idx_vaga_ubs ON vaga(ubs_id);
CREATE INDEX IF NOT EXISTS idx_agendamento_paciente ON agendamento(paciente_id);
CREATE INDEX IF NOT EXISTS idx_noticia_ubs ON noticia(ubs_id);
