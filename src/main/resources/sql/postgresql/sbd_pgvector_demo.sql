CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE sbd_pgvector_demo (
    id bigserial PRIMARY KEY,
    content varchar(255) DEFAULT NULL,
    embedding vector(3) DEFAULT NULL
);