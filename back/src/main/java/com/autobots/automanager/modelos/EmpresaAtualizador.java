package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;

import java.util.Set;

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

		atualizarUsuarios(empresa.getUsuarios(), atualizacao.getUsuarios());
		atualizarMercadorias(empresa.getMercadorias(), atualizacao.getMercadorias());
		atualizarServicos(empresa.getServicos(), atualizacao.getServicos());
		atualizarVendas(empresa.getVendas(), atualizacao.getVendas());
	}

	private void atualizarUsuarios(Set<Usuario> originais, Set<Usuario> atualizacoes) {
		for (Usuario uAtualizacao : atualizacoes) {
			for (Usuario uOriginal : originais) {
				if (uAtualizacao.getId() != null && uAtualizacao.getId().equals(uOriginal.getId())) {
					usuarioAtualizador.atualizar(uOriginal, uAtualizacao);
				}
			}
		}
	}

	private void atualizarMercadorias(Set<Mercadoria> originais, Set<Mercadoria> atualizacoes) {
		for (Mercadoria mAtualizacao : atualizacoes) {
			for (Mercadoria mOriginal : originais) {
				if (mAtualizacao.getId() != null && mAtualizacao.getId().equals(mOriginal.getId())) {
					mercadoriaAtualizador.atualizar(mOriginal, mAtualizacao);
				}
			}
		}
	}

	private void atualizarServicos(Set<Servico> originais, Set<Servico> atualizacoes) {
		for (Servico sAtualizacao : atualizacoes) {
			for (Servico sOriginal : originais) {
				if (sAtualizacao.getId() != null && sAtualizacao.getId().equals(sOriginal.getId())) {
					servicoAtualizador.atualizar(sOriginal, sAtualizacao);
				}
			}
		}
	}

	private void atualizarVendas(Set<Venda> originais, Set<Venda> atualizacoes) {
		for (Venda vAtualizacao : atualizacoes) {
			for (Venda vOriginal : originais) {
				if (vAtualizacao.getId() != null && vAtualizacao.getId().equals(vOriginal.getId())) {
					vendaAtualizador.atualizar(vOriginal, vAtualizacao);
				}
			}
		}
	}
}
