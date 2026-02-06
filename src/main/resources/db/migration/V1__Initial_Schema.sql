-- V1__Initial_Schema.sql
-- Schema inicial do Meu Postinho

-- Criar extension para UUID se necessário
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela UBS
CREATE TABLE IF NOT EXISTS ubs (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    codigo_cnes VARCHAR(10) UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    ativa BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ubs_estado_chk CHECK (estado ~ '^[A-Z]{2}$')
);

CREATE INDEX idx_ubs_cidade ON ubs(cidade);
CREATE INDEX idx_ubs_ativa ON ubs(ativa);

-- Tabela Usuario (base com herança SINGLE_TABLE)
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(25) NOT NULL DEFAULT 'MORADOR',
    cpf VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_nascimento VARCHAR(10),
    endereco VARCHAR(255) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE RESTRICT,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Campos específicos para Agente
    cns VARCHAR(20) UNIQUE,
    tipo_agente VARCHAR(25),
    status_cnes VARCHAR(25),
    data_verificacao_cnes TIMESTAMP,
    CONSTRAINT usuario_tipo_usuario_chk CHECK (tipo_usuario IN ('MORADOR', 'AGENTE'))
);

CREATE INDEX idx_usuario_cpf ON usuario(cpf);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_ubs_id ON usuario(ubs_id);
CREATE INDEX idx_usuario_tipo ON usuario(tipo_usuario);

-- Tabela de Roles de Usuário
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, role)
);

-- Tabela Medicamento
CREATE TABLE IF NOT EXISTS medicamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100) NOT NULL,
    posologia VARCHAR(255),
    unidade VARCHAR(50) NOT NULL,
    codigo_catmat VARCHAR(30) UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medicamento_nome ON medicamento(nome);
CREATE INDEX idx_medicamento_categoria ON medicamento(categoria);
CREATE INDEX idx_medicamento_ativo ON medicamento(ativo);

-- Tabela EstoqueMedicamento
CREATE TABLE IF NOT EXISTS estoque_medicamento (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE CASCADE,
    quantidade INTEGER NOT NULL DEFAULT 0,
    quantidade_minima INTEGER NOT NULL DEFAULT 10,
    data_entrada TIMESTAMP,
    data_vencimento TIMESTAMP,
    em_falta BOOLEAN NOT NULL DEFAULT false,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(ubs_id, medicamento_id),
    CONSTRAINT estoque_quantidade_chk CHECK (quantidade >= 0)
);

CREATE INDEX idx_estoque_ubs_id ON estoque_medicamento(ubs_id);
CREATE INDEX idx_estoque_medicamento_id ON estoque_medicamento(medicamento_id);
CREATE INDEX idx_estoque_em_falta ON estoque_medicamento(em_falta);

-- Tabela SolicitacaoMedicamento
CREATE TABLE IF NOT EXISTS solicitacao_medicamento (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE RESTRICT,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE RESTRICT,
    quantidade INTEGER NOT NULL,
    status VARCHAR(25) NOT NULL DEFAULT 'PENDENTE',
    justificativa_recusa TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_processamento TIMESTAMP,
    CONSTRAINT solicitacao_quantidade_chk CHECK (quantidade > 0),
    CONSTRAINT solicitacao_status_chk CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA', 'ENTREGUE'))
);

CREATE INDEX idx_solicitacao_usuario_id ON solicitacao_medicamento(usuario_id);
CREATE INDEX idx_solicitacao_medicamento_id ON solicitacao_medicamento(medicamento_id);
CREATE INDEX idx_solicitacao_ubs_id ON solicitacao_medicamento(ubs_id);
CREATE INDEX idx_solicitacao_status ON solicitacao_medicamento(status);

-- Tabela Vaga
CREATE TABLE IF NOT EXISTS vaga (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    data_vaga DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    profissional VARCHAR(150) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vaga_ubs_id ON vaga(ubs_id);
CREATE INDEX idx_vaga_data ON vaga(data_vaga);
CREATE INDEX idx_vaga_especialidade ON vaga(especialidade);
CREATE INDEX idx_vaga_disponivel ON vaga(disponivel);

-- Tabela Agendamento
CREATE TABLE IF NOT EXISTS agendamento (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    vaga_id BIGINT NOT NULL REFERENCES vaga(id) ON DELETE CASCADE,
    status VARCHAR(25) NOT NULL DEFAULT 'CONFIRMADO',
    observacoes TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_cancelamento TIMESTAMP,
    motivo_cancelamento VARCHAR(255),
    CONSTRAINT agendamento_status_chk CHECK (status IN ('CONFIRMADO', 'COMPARECEU', 'NAO_COMPARECEU', 'CANCELADO'))
);

CREATE INDEX idx_agendamento_usuario_id ON agendamento(usuario_id);
CREATE INDEX idx_agendamento_vaga_id ON agendamento(vaga_id);
CREATE INDEX idx_agendamento_status ON agendamento(status);

-- Tabela Noticia
CREATE TABLE IF NOT EXISTS noticia (
    id BIGSERIAL PRIMARY KEY,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    data_publicacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_noticia_ubs_id ON noticia(ubs_id);
CREATE INDEX idx_noticia_ativo ON noticia(ativo);
CREATE INDEX idx_noticia_data_publicacao ON noticia(data_publicacao);
