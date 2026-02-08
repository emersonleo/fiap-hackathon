-- V1__create_initial_schema.sql
-- Schema inicial do Meu Postinho - CORRIGIDO

-- Tabela UBS
CREATE TABLE ubs (
    id BIGSERIAL PRIMARY KEY,
    codigo_cnes VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(500),
    telefone VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    tipo VARCHAR(100),
    horario_funcionamento VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ubs_codigo_cnes ON ubs(codigo_cnes);

-- Tabela Usuario (Single Table Inheritance com DTYPE)
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    dtype VARCHAR(31) NOT NULL DEFAULT 'Usuario', -- 'Usuario' ou 'Agente'
    cpf VARCHAR(11) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255),
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    endereco VARCHAR(500),
    cep VARCHAR(8),
    ubs_id BIGINT REFERENCES ubs(id),
    
    -- Campos específicos de Agente (nullable para Usuario comum)
    cns VARCHAR(15),
    tipo VARCHAR(10), -- 'ACS' ou 'ACE'
    status_cnes VARCHAR(50),
    data_verificacao TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_agente_fields CHECK (
        (dtype = 'Agente' AND cns IS NOT NULL AND tipo IS NOT NULL) OR
        (dtype = 'Usuario' AND cns IS NULL AND tipo IS NULL)
    )
);

CREATE INDEX idx_usuario_cpf ON usuario(cpf);
CREATE INDEX idx_usuario_ubs ON usuario(ubs_id);
CREATE INDEX idx_usuario_dtype ON usuario(dtype);
CREATE INDEX idx_usuario_email ON usuario(email);

-- Tabela Medicamento
CREATE TABLE medicamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(100),
    unidade VARCHAR(50),
    codigo_catmat VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medicamento_nome ON medicamento(nome);
CREATE INDEX idx_medicamento_categoria ON medicamento(categoria);

-- Tabela Estoque de Medicamento
CREATE TABLE estoque_medicamento (
    id BIGSERIAL PRIMARY KEY,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE CASCADE,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    quantidade INT NOT NULL DEFAULT 0,
    data_ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lote VARCHAR(100),
    validade DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(medicamento_id, ubs_id),
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade >= 0)
);

CREATE INDEX idx_estoque_ubs ON estoque_medicamento(ubs_id);
CREATE INDEX idx_estoque_medicamento ON estoque_medicamento(medicamento_id);

-- Tabela Solicitação de Medicamento
CREATE TABLE solicitacao_medicamento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    medicamento_id BIGINT NOT NULL REFERENCES medicamento(id) ON DELETE CASCADE,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    quantidade INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_solicitacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_resposta TIMESTAMP,
    justificativa_recusa TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_quantidade_solicitacao CHECK (quantidade > 0),
    CONSTRAINT chk_status_solicitacao CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA'))
);

CREATE INDEX idx_solicitacao_status ON solicitacao_medicamento(status);
CREATE INDEX idx_solicitacao_paciente ON solicitacao_medicamento(paciente_id);
CREATE INDEX idx_solicitacao_ubs ON solicitacao_medicamento(ubs_id);

-- Tabela Vaga
CREATE TABLE vaga (
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    especialidade VARCHAR(100) NOT NULL,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    disponivel BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vaga_data ON vaga(data);
CREATE INDEX idx_vaga_ubs ON vaga(ubs_id);
CREATE INDEX idx_vaga_disponivel ON vaga(disponivel);

-- Tabela Agendamento
CREATE TABLE agendamento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    vaga_id BIGINT NOT NULL REFERENCES vaga(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_agendamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_confirmacao TIMESTAMP,
    compareceu BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(vaga_id), -- Uma vaga só pode ter um agendamento
    CONSTRAINT chk_status_agendamento CHECK (status IN ('AGENDADO', 'CANCELADO', 'COMPARECEU', 'FALTOU'))
);

CREATE INDEX idx_agendamento_paciente ON agendamento(paciente_id);
CREATE INDEX idx_agendamento_status ON agendamento(status);

-- Tabela Notícia
CREATE TABLE noticia (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    data_publicacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ubs_id BIGINT NOT NULL REFERENCES ubs(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_noticia_ubs ON noticia(ubs_id);
CREATE INDEX idx_noticia_data ON noticia(data_publicacao DESC);