package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.modelos.AdicionadorLinkEmail;
import com.autobots.automanager.modelos.EmailAtualizador;
import com.autobots.automanager.modelos.EmailSelecionador;
import com.autobots.automanager.repositorios.EmailRepositorio;

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
@RequestMapping("/email")
public class EmailControle {
	private final EmailRepositorio repositorio;
	private final EmailSelecionador selecionador;
	private final AdicionadorLinkEmail adicionador;

	public EmailControle(EmailRepositorio repositorio,
			EmailSelecionador selecionador,
			AdicionadorLinkEmail adicionador) {
		this.repositorio = repositorio;
		this.selecionador = selecionador;
		this.adicionador = adicionador;
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Email> obterEmail(@PathVariable long id) {
		List<Email> emails = repositorio.findAll();
		Email email = selecionador.selecionar(emails, id);
		if (email == null) {
			ResponseEntity<Email> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionador.adicionarLink(email);
			ResponseEntity<Email> resposta = new ResponseEntity<Email>(email, HttpStatus.FOUND);
			return resposta;
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Email>> obterEmails() {
		List<Email> emails = repositorio.findAll();
		if (emails.isEmpty()) {
			ResponseEntity<List<Email>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionador.adicionarLink(emails);
			ResponseEntity<List<Email>> resposta = new ResponseEntity<>(emails, HttpStatus.FOUND);
			return resposta;
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Void> cadastrarEmail(@RequestBody Email email) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (email.getId() == null) {
			repositorio.save(email);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/atualizar")
	public ResponseEntity<Void> atualizarEmail(@RequestBody Email atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Email email = repositorio.getById(atualizacao.getId());
		if (email != null) {
			EmailAtualizador atualizador = new EmailAtualizador();
			atualizador.atualizar(email, atualizacao);
			repositorio.save(email);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirEmail(@RequestBody Email exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Email email = repositorio.getById(exclusao.getId());
		if (email != null) {
			repositorio.delete(email);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
