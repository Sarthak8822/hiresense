CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector; -- requires pgvector image

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE resumes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title TEXT,
  file_url TEXT NOT NULL,
  parse_status TEXT DEFAULT 'PENDING',
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE resume_embeddings (
  resume_id UUID PRIMARY KEY REFERENCES resumes(id) ON DELETE CASCADE,
  model TEXT NOT NULL,
  vector VECTOR(1536) NOT NULL
);

CREATE TABLE jobs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  company TEXT, title TEXT, location TEXT,
  source_url TEXT, jd_text TEXT,
  seniority TEXT,
  salary_min INT, salary_max INT,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE job_embeddings (
  job_id UUID PRIMARY KEY REFERENCES jobs(id) ON DELETE CASCADE,
  model TEXT NOT NULL,
  vector VECTOR(1536) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_resume_vector ON resume_embeddings USING ivfflat (vector vector_cosine_ops);
CREATE INDEX IF NOT EXISTS idx_job_vector    ON job_embeddings    USING ivfflat (vector vector_cosine_ops);
