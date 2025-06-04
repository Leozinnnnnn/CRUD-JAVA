# 🎬 Projeto Locadora de Filmes em Java

Este é um sistema simples de cadastro e gerenciamento de clientes, filmes e funcionários, desenvolvido em Java com Swing. O projeto foi feito como parte da disciplina de desenvolvimento orientado a objetos do curso Ciência da Computação - USCS.

## 🛠 Tecnologias Utilizadas:
- Java
- Swing (interface gráfica)
- JDBC (conexão com banco de dados)
- MySQL
- NetBeans (IDE utilizada no desenvolvimento)
  
## 📂 Funcionalidades:
- ✅ Cadastro de Clientes
- ✅ Cadastro de Filmes
- ✅ Cadastro de Funcionários
- ✏️ Edição de registros
- 🗑️ Remoção de registros
- 📄 Listagem de dados em tempo real

---

## 🚀 Como Executar o Projeto

Abra o projeto no NetBeans.

Configure o arquivo ConexaoBD.java com os dados do seu banco de dados: 
String url = "jdbc:mysql://localhost:3306/nome_do_banco";
String usuario = "root";
String senha = "sua_senha";

Crie o banco de dados e as tabelas MySQL:
CREATE DATABASE locadora;
USE locadora;

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE filmes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100),
    genero VARCHAR(50),
    ano INT
);

CREATE TABLE funcionarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    cargo VARCHAR(50)
);

Execute a classe LocadoraApp para iniciar o sistema.
