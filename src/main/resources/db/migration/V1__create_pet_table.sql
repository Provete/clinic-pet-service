-- V1__create_pet_table.sql
CREATE TABLE pet (
                     id BIGSERIAL PRIMARY KEY,
                     name VARCHAR(50) NOT NULL,
                     birth_date DATE
);