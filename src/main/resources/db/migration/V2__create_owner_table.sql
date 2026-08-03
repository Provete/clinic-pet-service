-- V2__create_owner_table.sql (corrigida)
CREATE TABLE owner (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       phone VARCHAR(15) NOT NULL UNIQUE
);

ALTER TABLE pet ADD COLUMN owner_id BIGINT NOT NULL REFERENCES owner(id);