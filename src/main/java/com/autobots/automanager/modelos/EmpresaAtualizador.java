package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;

public class EmpresaAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private EnderecoAtualizador enderecoAtualizador = new EnderecoAtualizador();
	private UsuarioAtualizador usuarioAtualizador = new UsuarioAtualizador();
	private MercadoriaAtualizador mercadoriaAtualizador = new MercadoriaAtualizador();
	private ServicoAtualizador servicoAtualizador = new ServicoAtualizador();
	private VendaAtualizador vendaAtualizador = new VendaAtualizador();
	private TelefoneAtualizador telefoneAtualizador = new TelefoneAtualizador();

	private void atualizarDados(Empresa empresa, Empresa atualizacao) {
		if (!verificador.verificar(atualizacao.getRazaoSocial())) {
			empresa.setRazaoSocial(atualizacao.getRazaoSocial());
		}
		if (!verificador.verificar(atualizacao.getNomeFantasia())) {
			empresa.setNomeFantasia(atualizacao.getNomeFantasia());
		}
	}

	public void atualizar(Empresa empresa, Empresa atualizacao) {
		atualizarDados(empresa, atualizacao);
		enderecoAtualizador.atualizar(empresa.getEndereco(), atualizacao.getEndereco());
		telefoneAtualizador.atualizar(empresa.getTelefones(), atualizacao.getTelefones());
		
		// atualizar usuarios
		for (Usuario usuarioAtualizacao : atualizacao.getUsuarios()) {
			for (Usuario usuario : empresa.getUsuarios()) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == usuario.getId()) {
						usuarioAtualizador.atualizar(usuario, usuarioAtualizacao);
					}
				}
			}
		}
		
		for (Mercadoria mercadoriaAtualizacao : atualizacao.getMercadorias()) {
			for (Mercadoria mercadoria : empresa.getMercadorias()) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == mercadoria.getId()) {
						mercadoriaAtualizador.atualizar(mercadoria, mercadoriaAtualizacao);
					}
				}
			}
		}

		for (Servico servicoAtualizacao : atualizacao.getServicos()) {
			for (Servico servico: empresa.getServicos()) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == servico.getId()) {
						servicoAtualizador.atualizar(servico, servicoAtualizacao);
					}
				}
			}
		}
		
		for (Venda vendaAtualizacao : atualizacao.getVendas()) {
			for (Venda venda : empresa.getVendas()) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == venda.getId()) {
						vendaAtualizador.atualizar(venda, vendaAtualizacao);
					}
				}
			}
		}
	}
}
