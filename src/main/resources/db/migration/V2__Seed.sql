-- V2__Seed.sql
-- Seeds consolidadas (idempotentes)

-- UBS
INSERT INTO ubs (codigo_cnes, nome, endereco, cep, telefone, cidade, estado, latitude, longitude, ativa)
VALUES
('2000001', 'UBS Centro', 'Rua Principal, 100 - Centro', '01310100', '(11) 3000-0001', 'São Paulo', 'SP', -23.550520, -46.633308, true),
('2000002', 'UBS Jardim das Flores', 'Av. das Flores, 500 - Jardim das Flores', '01311000', '(11) 3000-0002', 'São Paulo', 'SP', -23.552520, -46.635308, true),
('2000003', 'UBS Vila Esperança', 'Rua da Esperança, 250 - Vila Esperança', '01312000', '(11) 3000-0003', 'São Paulo', 'SP', -23.554520, -46.637308, true),
('2000004', 'UBS Parque São Paulo', 'Av. São Paulo, 1000 - Parque São Paulo', '01313000', '(11) 3000-0004', 'São Paulo', 'SP', -23.556520, -46.639308, true),
('2000005', 'UBS Santa Rita', 'Rua Santa Rita, 75 - Santa Rita', '01314000', '(11) 3000-0005', 'São Paulo', 'SP', -23.558520, -46.641308, true)
ON CONFLICT (codigo_cnes) DO NOTHING;

-- Medicamentos
INSERT INTO medicamento (nome, categoria, unidade, codigo_catmat, ativo)
VALUES
('Paracetamol 500mg', 'Analgésico', 'comprimido', 'CAT001', true),
('Dipirona Sódica 500mg', 'Analgésico', 'comprimido', 'CAT002', true),
('Ibuprofeno 600mg', 'Anti-inflamatório', 'comprimido', 'CAT003', true),
('Amoxicilina 500mg', 'Antibiótico', 'cápsula', 'CAT004', true),
('Azitromicina 500mg', 'Antibiótico', 'comprimido', 'CAT005', true),
('Cefalexina 500mg', 'Antibiótico', 'cápsula', 'CAT006', true),
('Losartana Potássica 50mg', 'Anti-hipertensivo', 'comprimido', 'CAT007', true),
('Enalapril 10mg', 'Anti-hipertensivo', 'comprimido', 'CAT008', true),
('Atenolol 50mg', 'Anti-hipertensivo', 'comprimido', 'CAT009', true),
('Hidroclorotiazida 25mg', 'Diurético', 'comprimido', 'CAT010', true)
ON CONFLICT (codigo_catmat) DO NOTHING;
