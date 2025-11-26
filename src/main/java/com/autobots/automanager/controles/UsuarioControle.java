package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.modelos.AdicionadorLinkUsuario;
import com.autobots.automanager.modelos.UsuarioAtualizador;
import com.autobots.automanager.modelos.UsuarioSelecionador;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
	private final UsuarioRepositorio repositorio;
	private final UsuarioSelecionador selecionador;
	private final AdicionadorLinkUsuario adicionador;

	public UsuarioControle(
			UsuarioRepositorio repositorio,
			UsuarioSelecionador selecionador,
			AdicionadorLinkUsuario adicionador) {
		this.repositorio = repositorio;
		this.selecionador = selecionador;
		this.adicionador = adicionador;
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Usuario> obterUsuario(@PathVariable long id) {
		List<Usuario> usuarios = repositorio.findAll();
		Usuario usuario = selecionador.selecionar(usuarios, id);
		if (usuario == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(usuario);
			return new ResponseEntity<>(usuario, HttpStatus.FOUND);
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		List<Usuario> usuarios = repositorio.findAll();
		if (usuarios.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(usuarios);
			return new ResponseEntity<>(usuarios, HttpStatus.FOUND);
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Void> cadastrarUsuario(@RequestBody Usuario usuario) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (usuario.getId() == null) {
			boolean hasMercadorias = usuario.getMercadorias() != null && !usuario.getMercadorias().isEmpty();
			boolean hasVeiculos = usuario.getVeiculos() != null && !usuario.getVeiculos().isEmpty();
			if (hasMercadorias || hasVeiculos) {
				if (usuario.getPerfis() != null && usuario.getPerfis().contains(PerfilUsuario.FORNECEDOR)) {
					repositorio.save(usuario);
					status = HttpStatus.CREATED;
				} else {
					status = HttpStatus.FORBIDDEN;
				}
			} else {
				repositorio.save(usuario);
				status = HttpStatus.CREATED;
			}
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/atualizar")
	public ResponseEntity<Void> atualizarUsuario(@RequestBody Usuario atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Usuario usuario = repositorio.getById(atualizacao.getId());
		if (usuario != null) {
			UsuarioAtualizador atualizador = new UsuarioAtualizador();
			boolean addingMercadorias = atualizacao.getMercadorias() != null && !atualizacao.getMercadorias().isEmpty();
			boolean addingVeiculos = atualizacao.getVeiculos() != null && !atualizacao.getVeiculos().isEmpty();
			if (addingMercadorias || addingVeiculos) {
				boolean isFornecedor = (atualizacao.getPerfis() != null
						&& atualizacao.getPerfis().contains(PerfilUsuario.FORNECEDOR))
						|| (usuario.getPerfis() != null && usuario.getPerfis().contains(PerfilUsuario.FORNECEDOR));
				if (!isFornecedor) {
					return new ResponseEntity<>(HttpStatus.FORBIDDEN);
				}
			}
			atualizador.atualizar(usuario, atualizacao);
			repositorio.save(usuario);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirUsuario(@RequestBody Usuario exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Usuario usuario = repositorio.getById(exclusao.getId());
		if (usuario != null) {
			repositorio.delete(usuario);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
