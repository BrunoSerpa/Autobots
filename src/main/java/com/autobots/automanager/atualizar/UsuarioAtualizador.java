package com.autobots.automanager.atualizar;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public class UsuarioAtualizador {
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

	private DocumentoAtualizador atualizadorDocumento;
	private EnderecoAtualizador atualizadorEndereco;
	private TelefoneAtualizador atualizadorTelefone;
	private UsuarioRepositorio repositorioUsuario;

	public UsuarioAtualizador(DocumentoAtualizador atualizadorDocumento,
			EnderecoAtualizador atualizadorEndereco,
			TelefoneAtualizador atualizadorTelefone,
			UsuarioRepositorio repositorioUsuario) {
		this.atualizadorDocumento = atualizadorDocumento;
		this.atualizadorEndereco = atualizadorEndereco;
		this.atualizadorTelefone = atualizadorTelefone;
		this.repositorioUsuario = repositorioUsuario;
	}

	public Usuario atualizar(Usuario usuario, Usuario atualizacao) {
		if (atualizacao == null) {
			if (usuario != null) {
				repositorioUsuario.delete(usuario);
			}
			return null;
		}

		boolean novo = (usuario == null);
		if (novo) {
			usuario = new Usuario();
		}

		if (usuario == null) {
			return null;
		}

		if (!NULO.verificar(atualizacao.getNome()))
			usuario.setNome(atualizacao.getNome());

		usuario.setNomeSocial(atualizacao.getNomeSocial());
		usuario.setDataNascimento(atualizacao.getDataNascimento())

		usuario.setEndereco(atualizadorEndereco.atualizar(usuario.getEndereco(), atualizacao.getEndereco()));

		atualizadorDocumento.atualizar(usuario.getDocumentos(), atualizacao.getDocumentos());
		atualizadorTelefone.atualizar(usuario.getTelefones(), atualizacao.getTelefones());

		if (novo) {
			repositorioUsuario.save(usuario);
		}

		return usuario;
	}
}
