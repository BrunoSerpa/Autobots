package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.enumeracoes.TipoDocumento;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@SpringBootApplication
public class AutomanagerApplication implements CommandLineRunner {

    @Autowired
    private EmpresaRepositorio EmpresaRepositorio;

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Empresa
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial("Oficina Mecânica LTDA");
        empresa.setNomeFantasia("Auto Center Genérico");
        empresa.setCadastro(new Date());

        Endereco enderecoEmpresa = new Endereco();
        enderecoEmpresa.setEstado("SP");
        enderecoEmpresa.setCidade("São Paulo");
        enderecoEmpresa.setBairro("Centro");
        enderecoEmpresa.setRua("Rua Principal");
        enderecoEmpresa.setNumero("100");
        enderecoEmpresa.setCodigoPostal("00000-000");
        empresa.setEndereco(enderecoEmpresa);

        Telefone telefoneEmpresa = new Telefone();
        telefoneEmpresa.setDdd("11");
        telefoneEmpresa.setNumero("999999999");
        empresa.getTelefones().add(telefoneEmpresa);

        // Funcionário
        Usuario funcionario = new Usuario();
        funcionario.setNome("João da Silva");
        funcionario.setNomeSocial("João");
        funcionario.getPerfis().add(PerfilUsuario.FUNCIONARIO);

        Email emailFuncionario = new Email();
        emailFuncionario.setEndereco("funcionario@empresa.com");
        funcionario.getEmails().add(emailFuncionario);

        Endereco enderecoFuncionario = new Endereco();
        enderecoFuncionario.setEstado("SP");
        enderecoFuncionario.setCidade("São Paulo");
        enderecoFuncionario.setBairro("Bairro Genérico");
        enderecoFuncionario.setRua("Rua Secundária");
        enderecoFuncionario.setNumero("200");
        enderecoFuncionario.setCodigoPostal("11111-111");
        funcionario.setEndereco(enderecoFuncionario);

        Telefone telefoneFuncionario = new Telefone();
        telefoneFuncionario.setDdd("11");
        telefoneFuncionario.setNumero("988888888");
        funcionario.getTelefones().add(telefoneFuncionario);

        Documento cpfFuncionario = new Documento();
        cpfFuncionario.setDataEmissao(new Date());
        cpfFuncionario.setNumero("00011122233");
        cpfFuncionario.setTipo(TipoDocumento.CPF);
        funcionario.getDocumentos().add(cpfFuncionario);

        CredencialUsuarioSenha credencialFuncionario = new CredencialUsuarioSenha();
        credencialFuncionario.setInativo(false);
        credencialFuncionario.setNomeUsuario("funcionario1");
        credencialFuncionario.setSenha("senha123");
        credencialFuncionario.setCriacao(new Date());
        credencialFuncionario.setUltimoAcesso(new Date());
        funcionario.getCredenciais().add(credencialFuncionario);

        empresa.getUsuarios().add(funcionario);

        // Fornecedor
        Usuario fornecedor = new Usuario();
        fornecedor.setNome("Fornecedor de Peças LTDA");
        fornecedor.setNomeSocial("Loja de Peças");
        fornecedor.getPerfis().add(PerfilUsuario.FORNECEDOR);

        Email emailFornecedor = new Email();
        emailFornecedor.setEndereco("fornecedor@pecas.com");
        fornecedor.getEmails().add(emailFornecedor);

        CredencialUsuarioSenha credencialFornecedor = new CredencialUsuarioSenha();
        credencialFornecedor.setInativo(false);
        credencialFornecedor.setNomeUsuario("fornecedor1");
        credencialFornecedor.setSenha("senha123");
        credencialFornecedor.setCriacao(new Date());
        credencialFornecedor.setUltimoAcesso(new Date());
        fornecedor.getCredenciais().add(credencialFornecedor);

        Documento cnpjFornecedor = new Documento();
        cnpjFornecedor.setDataEmissao(new Date());
        cnpjFornecedor.setNumero("12345678000199");
        cnpjFornecedor.setTipo(TipoDocumento.CNPJ);
        fornecedor.getDocumentos().add(cnpjFornecedor);

        Endereco enderecoFornecedor = new Endereco();
        enderecoFornecedor.setEstado("RJ");
        enderecoFornecedor.setCidade("Rio de Janeiro");
        enderecoFornecedor.setBairro("Centro");
        enderecoFornecedor.setRua("Rua Comercial");
        enderecoFornecedor.setNumero("300");
        enderecoFornecedor.setCodigoPostal("22222-222");
        fornecedor.setEndereco(enderecoFornecedor);

        empresa.getUsuarios().add(fornecedor);

        // Mercadoria
        Mercadoria mercadoria = new Mercadoria();
        mercadoria.setCadastro(new Date());
        mercadoria.setFabricao(new Date());
        mercadoria.setNome("Pneu Aro 15");
        mercadoria.setValidade(new Date());
        mercadoria.setQuantidade(50);
        mercadoria.setValor(250.0);
        mercadoria.setDescricao("Pneu genérico para veículos de passeio");
        mercadoria.setOriginal(true);

        empresa.getMercadorias().add(mercadoria);
        fornecedor.getMercadorias().add(mercadoria);

        // Cliente
        Usuario cliente = new Usuario();
        cliente.setNome("Maria Oliveira");
        cliente.setNomeSocial("Maria");
        cliente.getPerfis().add(PerfilUsuario.CLIENTE);

        Email emailCliente = new Email();
        emailCliente.setEndereco("cliente@email.com");
        cliente.getEmails().add(emailCliente);

        Documento cpfCliente = new Documento();
        cpfCliente.setDataEmissao(new Date());
        cpfCliente.setNumero("99988877766");
        cpfCliente.setTipo(TipoDocumento.CPF);
        cliente.getDocumentos().add(cpfCliente);

        CredencialUsuarioSenha credencialCliente = new CredencialUsuarioSenha();
        credencialCliente.setInativo(false);
        credencialCliente.setNomeUsuario("cliente1");
        credencialCliente.setSenha("senha123");
        credencialCliente.setCriacao(new Date());
        credencialCliente.setUltimoAcesso(new Date());
        cliente.getCredenciais().add(credencialCliente);

        Endereco enderecoCliente = new Endereco();
        enderecoCliente.setEstado("SP");
        enderecoCliente.setCidade("São José dos Campos");
        enderecoCliente.setBairro("Centro");
        enderecoCliente.setRua("Rua Cliente");
        enderecoCliente.setNumero("400");
        enderecoCliente.setCodigoPostal("33333-333");
        cliente.setEndereco(enderecoCliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("XYZ-1234");
        veiculo.setModelo("Sedan Genérico");
        veiculo.setTipo(TipoVeiculo.SEDAN);
        veiculo.setProprietario(cliente);
        cliente.getVeiculos().add(veiculo);

        empresa.getUsuarios().add(cliente);

        // Serviços
        Servico servico1 = new Servico();
        servico1.setDescricao("Troca de pneus");
        servico1.setNome("Troca de pneus");
        servico1.setValor(100);
        servico1.setOriginal(true);

        Servico servico2 = new Servico();
        servico2.setDescricao("Alinhamento de direção");
        servico2.setNome("Alinhamento");
        servico2.setValor(80);
        servico2.setOriginal(true);

        empresa.getServicos().add(servico1);
        empresa.getServicos().add(servico2);

        // Venda
        Venda venda = new Venda();
        venda.setCadastro(new Date());
        venda.setCliente(cliente);
        venda.getMercadorias().add(mercadoria);
        venda.setIdentificacao("VENDA-001");
        venda.setFuncionario(funcionario);
        venda.getServicos().add(servico1);
        venda.getServicos().add(servico2);
        venda.setVeiculo(veiculo);
        veiculo.getVendas().add(venda);

        empresa.getVendas().add(venda);

        EmpresaRepositorio.save(empresa);
    }
}
