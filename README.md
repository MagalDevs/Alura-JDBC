# Curso de Java com JDBC: Integração com Banco de Dados

Este projeto foi desenvolvido durante o curso de **Integração Java com Banco de Dados**, com foco na criação de uma aplicação utilizando **JDBC (Java Database Connectivity)** e o banco de dados **MySQL**.

## 📚 Conteúdo Aprendido

### 🔗 Conexão com o Banco de Dados

* Integração da aplicação Java com o MySQL.
* Configuração do conector JDBC e resolução de erros comuns, como `Access denied for user 'root'@'localhost'`.

### 🏭 Padrão de Projeto Factory

* Implementação do **Factory Pattern** para encapsular a lógica de criação de conexões.
* Centralização da lógica de conexão com o banco em uma única classe.

### 💾 Inserção de Dados

* Uso da cláusula `INSERT` para salvar dados no banco.
* Criação da classe DAO (Data Access Object) para isolar a lógica de persistência.

### 📋 Listagem de Registros

* Leitura de dados usando `SELECT`.
* Criação de listagens com Java + SQL.
* Desafios de escalabilidade de conexões.
* Implementação de **Connection Pool** com HikariCP.

### 🔄 Atualização de Dados

* Uso da cláusula `UPDATE` para atualizar informações no banco (ex: depósito, saque, transferência).
* Manipulação de erros e controle de saldo.

### 🗑️ Exclusão de Dados

* Aplicação do `DELETE` para exclusão física de dados.
* Implementação de **exclusão lógica** (marcando como inativo em vez de deletar de fato).
* Discussões sobre problemas comuns, como `N+1` e parâmetros fora do índice.

---

## 🚀 Tecnologias Utilizadas

* **Java (JDK 17+)**
* **JDBC**
* **MySQL**
* **HikariCP** (para pool de conexões)

---

## 📂 Estrutura do Projeto

```bash
src/main/java/br/com/alura/bytebank/
├── BytebankApplication.java            # Classe principal da aplicação
├── ConnectionFactory.java              # Classe responsável por criar conexões com o banco
├── domain/
│   ├── cliente/
│   │   ├── Cliente.java                # Entidade Cliente
│   │   └── DadosCadastroCliente.java  # DTO para cadastro de cliente
│   └── conta/
│       ├── Conta.java                 # Entidade Conta
│       ├── ContaDAO.java             # Classe DAO para operações com Conta
│       ├── ContaService.java         # Regras de negócio para contas
│       └── DadosAberturaConta.java   # DTO para abertura de conta
├── RegraDeNegocioException.java      # Classe de exceção personalizada
```