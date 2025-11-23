package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.modelos.AdicionadorLinkServico;
import com.autobots.automanager.modelos.ServicoAtualizador;
import com.autobots.automanager.modelos.ServicoSelecionador;

public class ServicoControle {
	@Autowired
	private ServicoRepositorio repositorio;
	@Autowired
	private ServicoSelecionador selecionador;
	@Autowired
	private AdicionadorLinkServico adicionadorLink;

	@GetMapping("/servico/{id}")
	public ResponseEntity<Servico> obterServico(@PathVariable long id) {
		List<Servico> servicos = repositorio.findAll();
		Servico servico = selecionador.selecionar(servicos, id);
		if (servico == null) {
			ResponseEntity<Servico> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(servico);
			ResponseEntity<Servico> resposta = new ResponseEntity<Servico>(servico, HttpStatus.FOUND);
			return resposta;
		}
	}

	@GetMapping("/servicos")
	public ResponseEntity<List<Servico>> obterServicos() {
		List<Servico> servicos = repositorio.findAll();
		if (servicos.isEmpty()) {
			ResponseEntity<List<Servico>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(servicos);
			ResponseEntity<List<Servico>> resposta = new ResponseEntity<>(servicos, HttpStatus.FOUND);
			return resposta;
		}
	}

	@PostMapping("/servico/cadastro")
	public ResponseEntity<?> cadastrarServico(@RequestBody Servico servico) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (servico.getId() == null) {
			repositorio.save(servico);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/servico/atualizar")
	public ResponseEntity<?> atualizarServico(@RequestBody Servico atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Servico servico = repositorio.getById(atualizacao.getId());
		if (servico != null) {
			ServicoAtualizador atualizador = new ServicoAtualizador();
			atualizador.atualizar(servico, atualizacao);
			repositorio.save(servico);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/servico/excluir")
	public ResponseEntity<?> excluirServico(@RequestBody Servico exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Servico servico = repositorio.getById(exclusao.getId());
		if (servico != null) {
			repositorio.delete(servico);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
