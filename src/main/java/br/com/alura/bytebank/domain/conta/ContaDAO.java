package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection){
        this.conn = connection;
    }

    public void salvarConta(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, true ,cliente);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES(?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2 , BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listarContasAbertas() {
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta " +
                "WHERE esta_ativa = true";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                var clienteDados = new DadosCadastroCliente(resultSet.getString("cliente_nome"), resultSet.getString("cliente_cpf"), resultSet.getString("cliente_email"));
                var cliente =  new Cliente(clienteDados);
                contas.add(new Conta(resultSet.getInt("numero"), resultSet.getBigDecimal("saldo"), resultSet.getBoolean("esta_ativa"),cliente));
            }

            preparedStatement.close();
            resultSet.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contas;
    }

    public Conta buscarContaPorNumero(Integer numero) {
        Conta conta = null;
        String sql = "SELECT * FROM conta WHERE numero = ? AND esta_ativa = true";
        try {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var clienteDados = new DadosCadastroCliente(resultSet.getString("cliente_nome"), resultSet.getString("cliente_cpf"), resultSet.getString("cliente_email"));
                var cliente = new Cliente(clienteDados);
                conta = new Conta(resultSet.getInt("numero"), resultSet.getBigDecimal("saldo"), resultSet.getBoolean("esta_ativa"),cliente);
            }

            preparedStatement.close();
            resultSet.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor){
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ? AND esta_ativa = true";

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.executeUpdate();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Erro ao alterar o saldo da conta: " + e.getMessage(), e);
        }
    }

    public void encerrarConta(Integer numero) {
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao encerrar a conta: " + e.getMessage(), e);
        }
    }
}
