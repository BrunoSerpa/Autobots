package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkVenda;
import com.autobots.automanager.modelos.VendaAtualizador;
import com.autobots.automanager.modelos.VendaSelecionador;
import com.autobots.automanager.repositorios.VendaRepositorio;

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
@RequestMapping("/venda")
public class VendaControle {
	private final VendaRepositorio repositorio;
	private final VendaSelecionador selecionador;
	private final AdicionadorLinkVenda adicionador;

	public VendaControle(VendaRepositorio repositorio,
			VendaSelecionador selecionador,
			AdicionadorLinkVenda adicionador) {
		this.repositorio = repositorio;
		this.selecionador = selecionador;
		this.adicionador = adicionador;
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Venda> obterVenda(@PathVariable long id) {
		List<Venda> vendas = repositorio.findAll();
		Venda venda = selecionador.selecionar(vendas, id);
		if (venda == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(venda);
			return new ResponseEntity<>(venda, HttpStatus.FOUND);
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Venda>> obterVendas() {
		List<Venda> vendas = repositorio.findAll();
		if (vendas.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			adicionador.adicionarLink(vendas);
			return new ResponseEntity<>(vendas, HttpStatus.FOUND);
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<?> cadastrarVenda(@RequestBody Venda venda) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (venda.getId() == null) {
			repositorio.save(venda);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarVenda(@RequestBody Venda atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Venda venda = repositorio.getById(atualizacao.getId());
		if (venda != null) {
			VendaAtualizador atualizador = new VendaAtualizador();
			atualizador.atualizar(venda, atualizacao);
			repositorio.save(venda);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<?> excluirVenda(@RequestBody Venda exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Venda venda = repositorio.getById(exclusao.getId());
		if (venda != null) {
			repositorio.delete(venda);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
