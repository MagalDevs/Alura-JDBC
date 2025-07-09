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
        var conta = new Conta(dadosDaConta.numero(), cliente);

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

        String sql = "SELECT * FROM conta";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                var clienteDados = new DadosCadastroCliente(resultSet.getString("cliente_nome"), resultSet.getString("cliente_cpf"), resultSet.getString("cliente_email"));
                var cliente =  new Cliente(clienteDados);
                contas.add(new Conta(resultSet.getInt("numero"), cliente));
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
        String sql = "SELECT * FROM conta WHERE numero = ?";
        try {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, numero);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var clienteDados = new DadosCadastroCliente(resultSet.getString("cliente_nome"), resultSet.getString("cliente_cpf"), resultSet.getString("cliente_email"));
                var cliente = new Cliente(clienteDados);
                conta = new Conta(resultSet.getInt("numero"), cliente);
            }

            preparedStatement.close();
            resultSet.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conta;
    }
}
