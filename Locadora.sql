CREATE DATABASE IF NOT EXISTS locadora;
USE locadora;

-- Cria a tabela de clientes
CREATE TABLE IF NOT EXISTS clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL
);

-- Cria a tabela de filmes
CREATE TABLE IF NOT EXISTS filmes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  genero VARCHAR(100) NOT NULL
);

-- Cria a tabela de funcionários
CREATE TABLE IF NOT EXISTS funcionarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cargo VARCHAR(100) NOT NULL
);

SHOW TABLES;

-- Mostra os dados cadastrados nas tabelas
SELECT * FROM clientes;
SELECT * FROM filmes;
SELECT * FROM funcionarios;
