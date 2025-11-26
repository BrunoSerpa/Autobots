package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelos.AdicionadorLink;
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.modelos.EnderecoSelecionador;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

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
@RequestMapping("/endereco")
public class EnderecoControle {
	private final EnderecoRepositorio repositorio;
	private final EnderecoSelecionador selecionador;
	private final AdicionadorLink<Endereco> adicionador;

	public EnderecoControle(EnderecoRepositorio repositorio,
			EnderecoSelecionador selecionador,
			AdicionadorLink<Endereco> adicionador) {
		this.repositorio = repositorio;
		this.selecionador = selecionador;
		this.adicionador = adicionador;
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Endereco> obterEndereco(@PathVariable long id) {
		List<Endereco> enderecos = repositorio.findAll();
		Endereco endereco = selecionador.selecionar(enderecos, id);
		if (endereco == null) {
			return new ResponseEntity<Endereco>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(endereco);
			return new ResponseEntity<Endereco>(HttpStatus.FOUND);
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Endereco>> obterEnderecos() {
		List<Endereco> enderecos = repositorio.findAll();
		if (enderecos.isEmpty()) {
			return new ResponseEntity<List<Endereco>>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(enderecos);
			return new ResponseEntity<List<Endereco>>(HttpStatus.FOUND);
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<?> cadastrarEndereco(@RequestBody Endereco endereco) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (endereco.getId() == null) {
			repositorio.save(endereco);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);
	}

	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarEndereco(@RequestBody Endereco atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Endereco endereco = repositorio.getById(atualizacao.getId());
		if (endereco != null) {
			EnderecoAtualizador atualizador = new EnderecoAtualizador();
			atualizador.atualizar(endereco, atualizacao);
			repositorio.save(endereco);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<?> excluirEndereco(@RequestBody Endereco exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Endereco endereco = repositorio.getById(exclusao.getId());
		if (endereco != null) {
			repositorio.delete(endereco);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}