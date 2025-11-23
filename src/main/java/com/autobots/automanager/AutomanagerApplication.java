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
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@SpringBootApplication
public class AutomanagerApplication implements CommandLineRunner {

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("Car service toyota ltda");
		empresa.setNomeFantasia("Car service manutenção veicular");
		empresa.setCadastro(new Date());

		Endereco enderecoEmpresa = new Endereco();
		enderecoEmpresa.setEstado("São Paulo");
		enderecoEmpresa.setCidade("São Paulo");
		enderecoEmpresa.setBairro("Centro");
		enderecoEmpresa.setRua("Av. São João");
		enderecoEmpresa.setNumero("00");
		enderecoEmpresa.setCodigoPostal("01035-000");

		empresa.setEndereco(enderecoEmpresa);

		Telefone telefoneEmpresa = new Telefone();
		telefoneEmpresa.setDdd("011");
		telefoneEmpresa.setNumero("986454527");

		empresa.getTelefones().add(telefoneEmpresa);

		Usuario funcionario = new Usuario();
		funcionario.setNome("Pedro Alcântara de Bragança e Bourbon");
		funcionario.setNomeSocial("Dom Pedro");
		funcionario.getPerfis().add(PerfilUsuario.FUNCIONARIO);

		Email emailFuncionario = new Email();
		emailFuncionario.setEndereco("a@a.com");

		funcionario.getEmails().add(emailFuncionario);

		Endereco enderecoFuncionario = new Endereco();
		enderecoFuncionario.setEstado("São Paulo");
		enderecoFuncionario.setCidade("São Paulo");
		enderecoFuncionario.setBairro("Jardins");
		enderecoFuncionario.setRua("Av. São Gabriel");
		enderecoFuncionario.setNumero("00");
		enderecoFuncionario.setCodigoPostal("01435-001");

		funcionario.setEndereco(enderecoFuncionario);

		empresa.getUsuarios().add(funcionario);

		Telefone telefoneFuncionario = new Telefone();
		telefoneFuncionario.setDdd("011");
		telefoneFuncionario.setNumero("9854633728");

		funcionario.getTelefones().add(telefoneFuncionario);

		Documento cpf = new Documento();
		cpf.setDataEmissao(new Date());
		cpf.setNumero("856473819229");
		cpf.setTipo(TipoDocumento.CPF);

		funcionario.getDocumentos().add(cpf);

		CredencialUsuarioSenha credencialFuncionario = new CredencialUsuarioSenha();
		credencialFuncionario.setInativo(false);
		credencialFuncionario.setNomeUsuario("dompedrofuncionario");
		credencialFuncionario.setSenha("123456");
		credencialFuncionario.setCriacao(new Date());
		credencialFuncionario.setUltimoAcesso(new Date());

		funcionario.getCredenciais().add(credencialFuncionario);

		Usuario fornecedor = new Usuario();
		fornecedor.setNome("Componentes varejo de partes automotivas ltda");
		fornecedor.setNomeSocial("Loja do carro, vendas de componentes automotivos");
		fornecedor.getPerfis().add(PerfilUsuario.FORNECEDOR);

		Email emailFornecedor = new Email();
		emailFornecedor.setEndereco("f@f.com");

		fornecedor.getEmails().add(emailFornecedor);

		CredencialUsuarioSenha credencialFornecedor = new CredencialUsuarioSenha();
		credencialFornecedor.setInativo(false);
		credencialFornecedor.setNomeUsuario("dompedrofornecedor");
		credencialFornecedor.setSenha("123456");
		credencialFornecedor.setCriacao(new Date());
		credencialFornecedor.setUltimoAcesso(new Date());

		fornecedor.getCredenciais().add(credencialFornecedor);

		Documento cnpj = new Documento();
		cnpj.setDataEmissao(new Date());
		cnpj.setNumero("00014556000100");
		cnpj.setTipo(TipoDocumento.CNPJ);

		fornecedor.getDocumentos().add(cnpj);

		Endereco enderecoFornecedor = new Endereco();
		enderecoFornecedor.setEstado("Rio de Janeiro");
		enderecoFornecedor.setCidade("Rio de Janeiro");
		enderecoFornecedor.setBairro("Centro");
		enderecoFornecedor.setRua("Av. República do chile");
		enderecoFornecedor.setNumero("00");
		enderecoFornecedor.setCodigoPostal("20031-170");

		fornecedor.setEndereco(enderecoFornecedor);

		empresa.getUsuarios().add(fornecedor);
		
		Mercadoria rodaLigaLeve = new Mercadoria();
		rodaLigaLeve.setCadastro(new Date());
		rodaLigaLeve.setFabricao(new Date());
		rodaLigaLeve.setNome("Roda de liga leva modelo toyota etios");
		rodaLigaLeve.setValidade(new Date());
		rodaLigaLeve.setQuantidade(30);
		rodaLigaLeve.setValor(300.0);
		rodaLigaLeve.setDescricao("Roda de liga leve original de fábrica da toyta para modelos do tipo hatch");

		empresa.getMercadorias().add(rodaLigaLeve);

		fornecedor.getMercadorias().add(rodaLigaLeve);

		Usuario usuario = new Usuario();
		usuario.setNome("Pedro Alcântara de Bragança e Bourbon");
		usuario.setNomeSocial("Dom pedro usuario");
		usuario.getPerfis().add(PerfilUsuario.CLIENTE);

		Email emailUsuario = new Email();
		emailUsuario.setEndereco("c@c.com");

		usuario.getEmails().add(emailUsuario);

		Documento cpfUsuario = new Documento();
		cpfUsuario.setDataEmissao(new Date());
		cpfUsuario.setNumero("12584698533");
		cpfUsuario.setTipo(TipoDocumento.CPF);

		usuario.getDocumentos().add(cpfUsuario);

		CredencialUsuarioSenha credencialUsuario = new CredencialUsuarioSenha();
		credencialUsuario.setInativo(false);
		credencialUsuario.setNomeUsuario("dompedrousuario");
		credencialUsuario.setSenha("123456");
		credencialUsuario.setCriacao(new Date());
		credencialUsuario.setUltimoAcesso(new Date());

		usuario.getCredenciais().add(credencialUsuario);

		Endereco enderecoUsuario = new Endereco();
		enderecoUsuario.setEstado("São Paulo");
		enderecoUsuario.setCidade("São José dos Campos");
		enderecoUsuario.setBairro("Centro");
		enderecoUsuario.setRua("Av. Dr. Nelson D'Ávila");
		enderecoUsuario.setNumero("00");
		enderecoUsuario.setCodigoPostal("12245-070");

		usuario.setEndereco(enderecoUsuario);
		
		Veiculo veiculo = new Veiculo();
		veiculo.setPlaca("ABC-0000");
		veiculo.setModelo("corolla-cross");
		veiculo.setTipo(TipoVeiculo.SUV);
		veiculo.setProprietario(usuario);
		
		usuario.getVeiculos().add(veiculo);
		
		empresa.getUsuarios().add(usuario);

		Servico trocaRodas = new Servico();
		trocaRodas.setDescricao("Troca das rodas do carro por novas");
		trocaRodas.setNome("Troca de rodas");
		trocaRodas.setValor(50);

		Servico alinhamento = new Servico();
		alinhamento.setDescricao("Alinhamento das rodas do carro");
		alinhamento.setNome("Alinhamento de rodas");
		alinhamento.setValor(50);

		empresa.getServicos().add(trocaRodas);
		empresa.getServicos().add(alinhamento);

		Venda venda = new Venda();
		venda.setCadastro(new Date());
		venda.setUsuario(usuario);
		venda.getMercadorias().add(rodaLigaLeve);
		venda.setIdentificacao("1234698745");
		venda.setFuncionario(funcionario);
		venda.getServicos().add(trocaRodas);
		venda.getServicos().add(alinhamento);
		venda.setVeiculo(veiculo);
		veiculo.getVendas().add(venda);

		empresa.getVendas().add(venda);

		repositorioEmpresa.save(empresa);
		
		Mercadoria rodaLigaLeve2 = new Mercadoria();
		rodaLigaLeve2.setCadastro(new Date());
		rodaLigaLeve2.setFabricao(new Date());
		rodaLigaLeve2.setNome("Roda de liga leva modelo toyota etios");
		rodaLigaLeve2.setValidade(new Date());
		rodaLigaLeve2.setQuantidade(30);
		rodaLigaLeve2.setValor(300.0);
		rodaLigaLeve2.setDescricao("Roda de liga leve original de fábrica da toyta para modelos do tipo hatch");
		
		Servico alinhamento2 = new Servico();
		alinhamento2.setDescricao("Alinhamento das rodas do carro");
		alinhamento2.setNome("Alinhamento de rodas");
		alinhamento2.setValor(50);
		
		Servico balanceamento = new Servico();
		balanceamento.setDescricao("balanceamento das rodas do carro");
		balanceamento.setNome("balanceamento de rodas");
		balanceamento.setValor(30);
		
		Venda venda2 = new Venda();
		venda2.setCadastro(new Date());
		venda2.setUsuario(usuario);
		venda2.getMercadorias().add(rodaLigaLeve2);
		venda2.setIdentificacao("1234698749");
		venda2.setFuncionario(funcionario);
		venda2.getServicos().add(balanceamento);
		venda2.getServicos().add(alinhamento2);
		venda2.setVeiculo(veiculo);
		veiculo.getVendas().add(venda2);

		empresa.getVendas().add(venda2);
		
		repositorioEmpresa.save(empresa);

	}
}