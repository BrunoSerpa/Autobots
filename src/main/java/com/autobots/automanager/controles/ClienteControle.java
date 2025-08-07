package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.converter.ClienteConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	private static final String CLIENTE_NOT_FOUND = "Cliente não encontrado";
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private ClienteConverter conversor;

	@GetMapping("/{id}")
	public ClienteDTO obterCliente(@PathVariable Long id) {
		Cliente cliente = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(CLIENTE_NOT_FOUND));

		return conversor.convertToDto(cliente);
	}

	@GetMapping
	public List<ClienteDTO> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		return conversor.convertToDto(clientes);
	}

	@PostMapping
	public ResponseEntity<Void> cadastrarCliente(@RequestBody ClienteDTO cliente) {
		Cliente clienteCriado = conversor.convertToEntity(cliente);
		repositorio.save(clienteCriado);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteNovo) {
		Cliente cliente = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(CLIENTE_NOT_FOUND));
		ClienteAtualizador atualizador = new ClienteAtualizador();
		atualizador.atualizar(cliente, conversor.convertToEntity(clienteNovo));
		repositorio.save(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).body(conversor.convertToDto(cliente));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
		repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(CLIENTE_NOT_FOUND));

		repositorio.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
