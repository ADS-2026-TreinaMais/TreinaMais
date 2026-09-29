-- 1. TABELA DE USUÁRIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- 'PROFESSOR', 'ALUNO', 'ADMIN'
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO', -- 'ATIVO', 'INATIVO', 'BLOQUEADO'
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABELA DE PROFESSORES
CREATE TABLE IF NOT EXISTS professores (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    cref VARCHAR(30) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABELA DE ALUNOS
CREATE TABLE IF NOT EXISTS alunos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    professor_id BIGINT REFERENCES professores(id) ON DELETE SET NULL,
    cpf VARCHAR(14) UNIQUE,
    data_nascimento DATE,
    telefone VARCHAR(20),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. TABELA DE SESSÕES (Controle de autenticação / Tokens)
CREATE TABLE IF NOT EXISTS sessoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    revogado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. TABELA DE TREINOS (Fichas e planejamentos de treino)
CREATE TABLE IF NOT EXISTS treinos (
    id BIGSERIAL PRIMARY KEY,
    professor_id BIGINT REFERENCES professores(id) ON DELETE SET NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    objetivo VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. TABELA DE EXERCÍCIOS DO TREINO (Itens de cada treino)
CREATE TABLE IF NOT EXISTS exercicios_treino (
    id BIGSERIAL PRIMARY KEY,
    treino_id BIGINT NOT NULL REFERENCES treinos(id) ON DELETE CASCADE,
    nome_exercicio VARCHAR(100) NOT NULL,
    series INT NOT NULL DEFAULT 3,
    repeticoes VARCHAR(20) NOT NULL DEFAULT '10-12',
    carga VARCHAR(30),
    descanso_segundos INT DEFAULT 60,
    ordem INT NOT NULL DEFAULT 1,
    observacoes TEXT
);

-- 7. TABELA DE ASSOCIAÇÃO ALUNO_TREINO (Vínculo de fichas com alunos)
CREATE TABLE IF NOT EXISTS alunos_treino (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    treino_id BIGINT NOT NULL REFERENCES treinos(id) ON DELETE CASCADE,
    data_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    data_fim DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Índices para melhoria de desempenho
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_professores_usuario_id ON professores(usuario_id);
CREATE INDEX IF NOT EXISTS idx_alunos_usuario_id ON alunos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_alunos_professor_id ON alunos(professor_id);
CREATE INDEX IF NOT EXISTS idx_sessoes_usuario_id ON sessoes(usuario_id);
CREATE INDEX IF NOT EXISTS idx_sessoes_token_hash ON sessoes(token_hash);
CREATE INDEX IF NOT EXISTS idx_treinos_professor_id ON treinos(professor_id);
CREATE INDEX IF NOT EXISTS idx_exercicios_treino_id ON exercicios_treino(treino_id);
CREATE INDEX IF NOT EXISTS idx_alunos_treino_aluno ON alunos_treino(aluno_id);
CREATE INDEX IF NOT EXISTS idx_alunos_treino_treino ON alunos_treino(treino_id);
