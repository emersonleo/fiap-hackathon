-- V2__insert_seed_data.sql
-- Seeds iniciais: UBS mockadas e Medicamentos RENAME

-- Inserir UBS mockadas (dados fictícios realistas)
INSERT INTO ubs (codigo_cnes, nome, endereco, telefone, latitude, longitude, tipo, horario_funcionamento) VALUES
('2000001', 'UBS Centro', 'Rua Principal, 100 - Centro', '(11) 3000-0001', -23.550520, -46.633308, 'Unidade Básica de Saúde', '07:00-19:00'),
('2000002', 'UBS Jardim das Flores', 'Av. das Flores, 500 - Jardim das Flores', '(11) 3000-0002', -23.552520, -46.635308, 'Unidade Básica de Saúde', '07:00-19:00'),
('2000003', 'UBS Vila Esperança', 'Rua da Esperança, 250 - Vila Esperança', '(11) 3000-0003', -23.554520, -46.637308, 'Unidade Básica de Saúde', '07:00-17:00'),
('2000004', 'UBS Parque São Paulo', 'Av. São Paulo, 1000 - Parque São Paulo', '(11) 3000-0004', -23.556520, -46.639308, 'Unidade Básica de Saúde', '07:00-19:00'),
('2000005', 'UBS Santa Rita', 'Rua Santa Rita, 75 - Santa Rita', '(11) 3000-0005', -23.558520, -46.641308, 'Unidade Básica de Saúde', '07:00-17:00');

-- Inserir Medicamentos RENAME (Relação Nacional de Medicamentos Essenciais)
INSERT INTO medicamento (nome, categoria, unidade, codigo_catmat) VALUES
-- Analgésicos e Antipiréticos
('Paracetamol 500mg', 'Analgésico', 'comprimido', 'CAT001'),
('Dipirona Sódica 500mg', 'Analgésico', 'comprimido', 'CAT002'),
('Ibuprofeno 600mg', 'Anti-inflamatório', 'comprimido', 'CAT003'),

-- Antibióticos
('Amoxicilina 500mg', 'Antibiótico', 'cápsula', 'CAT004'),
('Azitromicina 500mg', 'Antibiótico', 'comprimido', 'CAT005'),
('Cefalexina 500mg', 'Antibiótico', 'cápsula', 'CAT006'),

-- Cardiovasculares
('Losartana Potássica 50mg', 'Anti-hipertensivo', 'comprimido', 'CAT007'),
('Enalapril 10mg', 'Anti-hipertensivo', 'comprimido', 'CAT008'),
('Atenolol 50mg', 'Anti-hipertensivo', 'comprimido', 'CAT009'),
('Hidroclorotiazida 25mg', 'Diurético', 'comprimido', 'CAT010'),
('Sinvastatina 20mg', 'Hipolipemiante', 'comprimido', 'CAT011'),
('AAS 100mg', 'Antiagregante plaquetário', 'comprimido', 'CAT012'),
('Captopril 25mg', 'Anti-hipertensivo', 'comprimido', 'CAT013'),
('Amlodipina 5mg', 'Anti-hipertensivo', 'comprimido', 'CAT014'),

-- Diabetes
('Metformina 850mg', 'Antidiabético', 'comprimido', 'CAT015'),
('Glibenclamida 5mg', 'Antidiabético', 'comprimido', 'CAT016'),
('Insulina NPH 100UI/mL', 'Antidiabético', 'frasco 10mL', 'CAT017'),

-- Gastrointestinais
('Omeprazol 20mg', 'Antiulceroso', 'cápsula', 'CAT018'),
('Ranitidina 150mg', 'Antiulceroso', 'comprimido', 'CAT019'),
('Simeticona 40mg', 'Antiflatulento', 'comprimido', 'CAT020'),

-- Respiratórios
('Salbutamol 100mcg', 'Broncodilatador', 'spray', 'CAT021'),
('Beclometasona 250mcg', 'Corticoide inalatório', 'spray', 'CAT022'),
('Prednisolona 20mg', 'Corticoide oral', 'comprimido', 'CAT023'),

-- Antiparasitários
('Albendazol 400mg', 'Antiparasitário', 'comprimido', 'CAT024'),
('Mebendazol 100mg', 'Antiparasitário', 'comprimido', 'CAT025'),

-- Vitaminas e Suplementos
('Sulfato Ferroso 40mg', 'Suplemento ferroso', 'comprimido', 'CAT026'),
('Ácido Fólico 5mg', 'Vitamina', 'comprimido', 'CAT027'),
('Complexo B', 'Vitamina', 'comprimido', 'CAT028'),

-- Outros
('Dexametasona 4mg', 'Corticoide', 'comprimido', 'CAT029'),
('Diclofenaco Sódico 50mg', 'Anti-inflamatório', 'comprimido', 'CAT030'),
('Levotiroxina 50mcg', 'Hormônio tireoidiano', 'comprimido', 'CAT031'),
('Fluconazol 150mg', 'Antifúngico', 'cápsula', 'CAT032'),
('Metoprolol 100mg', 'Beta-bloqueador', 'comprimido', 'CAT033');

-- Popular estoque inicial (distribuição variada entre UBS)
-- UBS Centro - estoque completo
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, data_ultima_atualizacao)
SELECT 1, id, 
    CASE 
        WHEN id % 3 = 0 THEN 150
        WHEN id % 3 = 1 THEN 200
        ELSE 100
    END,
    CURRENT_TIMESTAMP
FROM medicamento;

-- UBS Jardim das Flores - estoque médio
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, data_ultima_atualizacao)
SELECT 2, id, 
    CASE 
        WHEN id % 2 = 0 THEN 80
        ELSE 120
    END,
    CURRENT_TIMESTAMP
FROM medicamento
WHERE id <= 25;

-- UBS Vila Esperança - estoque variado
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, data_ultima_atualizacao)
SELECT 3, id, 
    CASE 
        WHEN id % 4 = 0 THEN 50
        WHEN id % 4 = 1 THEN 100
        WHEN id % 4 = 2 THEN 150
        ELSE 75
    END,
    CURRENT_TIMESTAMP
FROM medicamento
WHERE id <= 20;

-- UBS Parque São Paulo - foco em cardiovasculares e diabetes
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, data_ultima_atualizacao)
SELECT 4, id, 200, CURRENT_TIMESTAMP
FROM medicamento
WHERE categoria IN ('Anti-hipertensivo', 'Antidiabético', 'Diurético', 'Hipolipemiante');

-- UBS Santa Rita - medicamentos básicos
INSERT INTO estoque_medicamento (ubs_id, medicamento_id, quantidade, data_ultima_atualizacao)
SELECT 5, id, 100, CURRENT_TIMESTAMP
FROM medicamento
WHERE categoria IN ('Analgésico', 'Antibiótico', 'Anti-inflamatório')
   OR nome LIKE '%Paracetamol%' 
   OR nome LIKE '%Dipirona%';