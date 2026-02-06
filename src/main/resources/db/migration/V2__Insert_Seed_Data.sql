-- V2__Insert_Seed_Data.sql
-- Seeds iniciais: UBS mockadas e Medicamentos RENAME

-- Inserir UBS mockadas
INSERT INTO ubs (nome, codigo_cnes, endereco, cep, telefone, cidade, estado, latitude, longitude, ativa)
VALUES 
    ('UBS Parque São Vicente', '3509502', 'Rua São Vicente, 100', '04561-100', '(11) 3333-1111', 'São Paulo', 'SP', -23.5505, -46.6333, true),
    ('UBS Vila Mariana', '3509503', 'Av. Paulista, 500', '01311-000', '(11) 3333-2222', 'São Paulo', 'SP', -23.5615, -46.6560, true),
    ('UBS Centro', '3509504', 'Rua 15 de Novembro, 1000', '01038-001', '(11) 3333-3333', 'São Paulo', 'SP', -23.5505, -46.6361, true),
    ('UBS Zona Leste', '3509505', 'Rua Azevedo, 800', '03165-000', '(11) 3333-4444', 'São Paulo', 'SP', -23.5500, -46.4500, true),
    ('UBS Zona Norte', '3509506', 'Avenida Nações Unidas, 2000', '02311-100', '(11) 3333-5555', 'São Paulo', 'SP', -23.4700, -46.5000, true);

-- Inserir Medicamentos (lista RENAME simulada)
INSERT INTO medicamento (nome, descricao, categoria, posologia, unidade, codigo_catmat, ativo)
VALUES
    ('Paracetamol 500mg', 'Analgésico e antitérmico', 'Analgésico', '1 comprimido de 6 em 6 horas', 'Comprimido', 'CAT001', true),
    ('Ibuprofeno 600mg', 'Anti-inflamatório e analgésico', 'Anti-inflamatório', '1 comprimido de 8 em 8 horas', 'Comprimido', 'CAT002', true),
    ('Losartana 50mg', 'Antihipertensivo', 'Cardiovascular', '1 comprimido ao dia', 'Comprimido', 'CAT003', true),
    ('Enalapril 10mg', 'Inibidor da ECA para hipertensão', 'Cardiovascular', '1 comprimido ao dia', 'Comprimido', 'CAT004', true),
    ('Metformina 500mg', 'Antidiabético', 'Endocrinologia', '1 comprimido 2-3x ao dia', 'Comprimido', 'CAT005', true),
    ('Amoxicilina 500mg', 'Antibiótico penicilínico', 'Antibiótico', '1 cápsula de 8 em 8 horas', 'Cápsula', 'CAT006', true),
    ('Azitromicina 500mg', 'Antibiótico macrolídeo', 'Antibiótico', '1 comprimido ao dia', 'Comprimido', 'CAT007', true),
    ('Omeprazol 20mg', 'Inibidor de bomba de prótons', 'Gastro', '1 cápsula ao dia', 'Cápsula', 'CAT008', true),
    ('Ranitidina 150mg', 'Antagonista de receptor H2', 'Gastro', '1 comprimido 2x ao dia', 'Comprimido', 'CAT009', true),
    ('Dipirona Gotas', 'Analgésico pediátrico', 'Analgésico', 'Conforme prescrição médica', 'Solução', 'CAT010', true),
    ('Ambroxol Xarope', 'Mucolítico expectorante', 'Respiratório', '1 colher de chá 3x ao dia', 'Xarope', 'CAT011', true),
    ('Salbutamol Inalador', 'Broncodilatador beta-2 agonista', 'Respiratório', '2 jatos conforme necessário', 'Aerossol', 'CAT012', true),
    ('Amlodipina 5mg', 'Bloqueador de canal de cálcio', 'Cardiovascular', '1 comprimido ao dia', 'Comprimido', 'CAT013', true),
    ('Sinvastatina 20mg', 'Estatina para colesterol', 'Cardiovascular', '1 comprimido à noite', 'Comprimido', 'CAT014', true),
    ('Vitamina D 1000UI', 'Suplemento de vitamina D', 'Vitaminas', '1 gota ao dia', 'Solução', 'CAT015', true),
    ('Ferro Sulfato 25mg', 'Suplemento de ferro', 'Hematológico', '1 comprimido ao dia', 'Comprimido', 'CAT016', true),
    ('Cefalexina 500mg', 'Antibiótico cefalosporina', 'Antibiótico', '1 cápsula de 6 em 6 horas', 'Cápsula', 'CAT017', true),
    ('Dipirona 500mg', 'Analgésico anti-inflamatório', 'Analgésico', '1 comprimido de 6 em 6 horas', 'Comprimido', 'CAT018', true),
    ('Dexametasona 4mg', 'Corticosteroide', 'Inflamação', 'Conforme prescrição médica', 'Comprimido', 'CAT019', true),
    ('Prednisona 5mg', 'Corticosteroide', 'Inflamação', 'Conforme prescrição médica', 'Comprimido', 'CAT020', true),
    ('Metoprolol 100mg', 'Beta bloqueador', 'Cardiovascular', '1 comprimido 2x ao dia', 'Comprimido', 'CAT021', true),
    ('Atorvastatina 20mg', 'Estatina para colesterol', 'Cardiovascular', '1 comprimido à noite', 'Comprimido', 'CAT022', true),
    ('Captopril 25mg', 'Inibidor da ECA', 'Cardiovascular', '1 comprimido 3x ao dia', 'Comprimido', 'CAT023', true),
    ('Hidroclorotiazida 25mg', 'Diurético tiazida', 'Cardiovascular', '1 comprimido ao dia', 'Comprimido', 'CAT024', true),
    ('Fluconazol 150mg', 'Antifúngico', 'Antifúngico', 'Conforme prescrição médica', 'Cápsula', 'CAT025', true);

-- Inserir alguns estoques iniciais (cada medicamento em uma UBS)
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, quantidade_minima, em_falta)
SELECT ubs.id, med.id, 100, 20, false
FROM ubs 
CROSS JOIN medicamento med
LIMIT 50;
