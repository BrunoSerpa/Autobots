package com.autobots.automanager.controles;

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

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.servicos.EnderecoServico;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	private EnderecoServico enderecoServico;

	public EnderecoControle(EnderecoServico enderecoServico) {
		this.enderecoServico = enderecoServico;
	}

	@GetMapping("/{id}")
	public ResponseEntity<EnderecoDTO> procurarEndereco(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(enderecoServico.procurar(id));
	}

	@GetMapping
	public ResponseEntity<List<EnderecoDTO>> todosEnderecos() {
		return ResponseEntity.status(HttpStatus.OK).body(enderecoServico.todos());
	}

	@PostMapping("/{idCliente}")
	public ResponseEntity<Void> cadastroEndereco(@PathVariable Long idCliente, @RequestBody EnderecoDTO endereco) {
		enderecoServico.cadastro(idCliente, endereco);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<EnderecoDTO> atualizarEndereco(@PathVariable Long id, @RequestBody EnderecoDTO endereco) {
		return ResponseEntity.status(HttpStatus.CREATED).body(enderecoServico.atualizar(id, endereco));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirEndereco(@PathVariable Long id) {
		enderecoServico.excluir(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
