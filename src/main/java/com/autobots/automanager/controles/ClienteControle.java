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

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	private ClienteServico clienteServico;

	public ClienteControle(ClienteServico clienteServico) {
		this.clienteServico = clienteServico;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> procurarCliente(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(clienteServico.procurar(id));
	}

	@GetMapping
	public ResponseEntity<List<ClienteDTO>> todosClientes() {
		return ResponseEntity.status(HttpStatus.OK).body(clienteServico.todos());
	}

	@PostMapping
	public ResponseEntity<Void> cadastroCliente(@RequestBody ClienteDTO cliente) {
		clienteServico.cadastro(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping
	public ResponseEntity<ClienteDTO> atualizarCliente(@RequestBody ClienteDTO cliente) {
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteServico.atualizar(cliente));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
		clienteServico.excluir(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
