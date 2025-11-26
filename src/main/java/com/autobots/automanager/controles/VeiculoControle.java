package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.modelos.AdicionadorLinkVeiculo;
import com.autobots.automanager.modelos.VeiculoAtualizador;
import com.autobots.automanager.modelos.VeiculoSelecionador;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

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
@RequestMapping("/veiculo")
public class VeiculoControle {
	private final VeiculoRepositorio repositorio;
	private final VeiculoSelecionador selecionador;
	private final AdicionadorLinkVeiculo adicionador;

	public VeiculoControle(VeiculoRepositorio repositorio,
			VeiculoSelecionador selecionador,
			AdicionadorLinkVeiculo adicionador) {
		this.repositorio = repositorio;
		this.selecionador = selecionador;
		this.adicionador = adicionador;
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Veiculo> obterVeiculo(@PathVariable long id) {
		List<Veiculo> veiculos = repositorio.findAll();
		Veiculo veiculo = selecionador.selecionar(veiculos, id);
		if (veiculo == null) {
			ResponseEntity<Veiculo> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionador.adicionarLink(veiculo);
			ResponseEntity<Veiculo> resposta = new ResponseEntity<Veiculo>(veiculo, HttpStatus.FOUND);
			return resposta;
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Veiculo>> obterVeiculos() {
		List<Veiculo> veiculos = repositorio.findAll();
		if (veiculos.isEmpty()) {
			ResponseEntity<List<Veiculo>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionador.adicionarLink(veiculos);
			ResponseEntity<List<Veiculo>> resposta = new ResponseEntity<>(veiculos, HttpStatus.FOUND);
			return resposta;
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<?> cadastrarVeiculo(@RequestBody Veiculo veiculo) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (veiculo.getId() == null) {
			if (veiculo.getProprietario() != null && veiculo.getProprietario().getPerfis() != null
					&& veiculo.getProprietario().getPerfis().contains(PerfilUsuario.FORNECEDOR)) {
				repositorio.save(veiculo);
				status = HttpStatus.CREATED;
			} else {
				status = HttpStatus.FORBIDDEN;
			}
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarVeiculo(@RequestBody Veiculo atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Veiculo veiculo = repositorio.getById(atualizacao.getId());
		if (veiculo != null) {
			VeiculoAtualizador atualizador = new VeiculoAtualizador();
			atualizador.atualizar(veiculo, atualizacao);
			repositorio.save(veiculo);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<?> excluirVeiculo(@RequestBody Veiculo exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Veiculo veiculo = repositorio.getById(exclusao.getId());
		if (veiculo != null) {
			repositorio.delete(veiculo);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
