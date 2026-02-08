-- V3__insert_test_users.sql
-- Usuários de teste para desenvolvimento e demonstração

-- SENHA PADRÃO PARA TODOS: "senha123"
-- Hash BCrypt (strength 12): $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW

-- Inserir Moradores de teste (um para cada UBS)
INSERT INTO usuario (dtype, cpf, nome, telefone, email, senha, data_nascimento, endereco, cep, ubs_id) VALUES
('Usuario', '12345678901', 'João Silva', '11987654321', 'joao.silva@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1985-03-15', 'Rua das Acácias, 123', '01310100', 1),
('Usuario', '23456789012', 'Maria Santos', '11987654322', 'maria.santos@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1990-07-20', 'Av. Brasil, 456', '01311000', 2),
('Usuario', '34567890123', 'Pedro Oliveira', '11987654323', 'pedro.oliveira@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1978-11-10', 'Rua Esperança, 789', '01312000', 3),
('Usuario', '45678901234', 'Ana Costa', '11987654324', 'ana.costa@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1995-05-25', 'Av. Paulista, 1000', '01313000', 4),
('Usuario', '56789012345', 'Carlos Pereira', '11987654325', 'carlos.pereira@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1988-09-30', 'Rua Santa Rita, 200', '01314000', 5);

-- Inserir Agentes Comunitários de Saúde (um para cada UBS)
INSERT INTO usuario (dtype, cpf, nome, telefone, email, senha, data_nascimento, endereco, cep, ubs_id, cns, tipo, status_cnes, data_verificacao) VALUES
('Agente', '98765432101', 'Fernanda Lima (ACS)', '11987654331', 'fernanda.lima@ubs.gov.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1982-01-12', 'Rua Principal, 100', '01310100', 1, '123456789012345', 'ACS', 'ATIVO', CURRENT_TIMESTAMP),
('Agente', '87654321012', 'Roberto Souza (ACS)', '11987654332', 'roberto.souza@ubs.gov.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1987-04-18', 'Av. das Flores, 500', '01311000', 2, '234567890123456', 'ACS', 'ATIVO', CURRENT_TIMESTAMP),
('Agente', '76543210123', 'Juliana Ferreira (ACE)', '11987654333', 'juliana.ferreira@ubs.gov.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1991-08-22', 'Rua da Esperança, 250', '01312000', 3, '345678901234567', 'ACE', 'ATIVO', CURRENT_TIMESTAMP),
('Agente', '65432101234', 'Marcos Almeida (ACS)', '11987654334', 'marcos.almeida@ubs.gov.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1984-12-05', 'Av. São Paulo, 1000', '01313000', 4, '456789012345678', 'ACS', 'ATIVO', CURRENT_TIMESTAMP),
('Agente', '54321012345', 'Patricia Rocha (ACS)', '11987654335', 'patricia.rocha@ubs.gov.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIVEmgRWiW', '1989-06-14', 'Rua Santa Rita, 75', '01314000', 5, '567890123456789', 'ACS', 'ATIVO', CURRENT_TIMESTAMP);

-- Inserir algumas vagas de exemplo para testes de agendamento
INSERT INTO vaga (data, hora, especialidade, ubs_id, disponivel) VALUES
-- UBS Centro - próxima semana
(CURRENT_DATE + INTERVAL '7 days', '08:00:00', 'Clínica Geral', 1, true),
(CURRENT_DATE + INTERVAL '7 days', '09:00:00', 'Clínica Geral', 1, true),
(CURRENT_DATE + INTERVAL '7 days', '10:00:00', 'Pediatria', 1, true),
(CURRENT_DATE + INTERVAL '8 days', '08:00:00', 'Clínica Geral', 1, true),
(CURRENT_DATE + INTERVAL '8 days', '14:00:00', 'Ginecologia', 1, true),

-- UBS Jardim das Flores
(CURRENT_DATE + INTERVAL '7 days', '08:00:00', 'Clínica Geral', 2, true),
(CURRENT_DATE + INTERVAL '7 days', '09:00:00', 'Cardiologia', 2, true),
(CURRENT_DATE + INTERVAL '8 days', '08:00:00', 'Clínica Geral', 2, true),

-- UBS Vila Esperança
(CURRENT_DATE + INTERVAL '7 days', '08:00:00', 'Clínica Geral', 3, true),
(CURRENT_DATE + INTERVAL '7 days', '10:00:00', 'Pediatria', 3, true),

-- UBS Parque São Paulo
(CURRENT_DATE + INTERVAL '7 days', '08:00:00', 'Clínica Geral', 4, true),
(CURRENT_DATE + INTERVAL '7 days', '14:00:00', 'Endocrinologia', 4, true),

-- UBS Santa Rita
(CURRENT_DATE + INTERVAL '7 days', '08:00:00', 'Clínica Geral', 5, true),
(CURRENT_DATE + INTERVAL '8 days', '08:00:00', 'Clínica Geral', 5, true);

-- Inserir algumas notícias de exemplo
INSERT INTO noticia (titulo, conteudo, ubs_id, data_publicacao) VALUES
('Campanha de Vacinação contra Gripe', 'A UBS Centro informa que a campanha de vacinação contra gripe está disponível para idosos e grupos prioritários. Horário: 8h às 17h.', 1, CURRENT_TIMESTAMP),
('Mutirão de Saúde - Sábado', 'No próximo sábado teremos mutirão de exames preventivos. Não é necessário agendamento. Venha cedo!', 1, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Grupo de Diabéticos', 'Toda terça-feira às 14h temos encontro do grupo de apoio a diabéticos. Participe!', 2, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Novos Horários de Atendimento', 'A partir do dia 15, a UBS Vila Esperança funcionará também aos sábados, das 8h às 12h.', 3, CURRENT_TIMESTAMP),
('Falta de Medicamento - Previsão de Chegada', 'Informamos que o medicamento Losartana 50mg está temporariamente indisponível. Previsão de chegada: próxima semana.', 4, CURRENT_TIMESTAMP - INTERVAL '3 days');
