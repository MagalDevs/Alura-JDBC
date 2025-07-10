package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listarContasAbertas();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        return buscarContaPorNumero(numeroDaConta).getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).salvarConta(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RuntimeException("Saldo insuficiente!");
        }

        if (conta.getEstaAtiva() == false) {
            throw new RegraDeNegocioException("Conta não está ativa!");
        }

        BigDecimal novoSaldo = conta.getSaldo().subtract(valor);
        alterar(conta, novoSaldo);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        if (conta.getEstaAtiva() == false) {
            throw new RegraDeNegocioException("Conta não está ativa!");
        }

        BigDecimal novoSaldo = conta.getSaldo().add(valor);
        alterar(conta, novoSaldo);
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        realizarSaque(numeroDaContaOrigem, valor);
        realizarDeposito(numeroDaContaDestino, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        var conn = connection.recuperarConexao();
        new ContaDAO(conn).encerrarConta(numeroDaConta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).buscarContaPorNumero(numero);
        if (conta != null) {
            return conta;
        }else {
            throw new RegraDeNegocioException("Conta não encontrada!");
        }
    }

    private void alterar(Conta conta, BigDecimal novoSaldo) {
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(conta.getNumero(), novoSaldo);
    }
}
