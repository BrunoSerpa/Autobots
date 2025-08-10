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

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.servicos.TelefoneServico;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	private TelefoneServico telefoneServico;

	public TelefoneControle(TelefoneServico telefoneServico) {
		this.telefoneServico = telefoneServico;
	}

	@GetMapping("/{id}")
	public ResponseEntity<TelefoneDTO> procurarTelefone(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(telefoneServico.procurar(id));
	}

	@GetMapping
	public ResponseEntity<List<TelefoneDTO>> todosTelefones() {
		return ResponseEntity.status(HttpStatus.OK).body(telefoneServico.todos());
	}

	@PostMapping("/{idCliente}")
	public ResponseEntity<Void> cadastroTelefone(@PathVariable Long idCliente, @RequestBody TelefoneDTO telefone) {
		telefoneServico.cadastro(idCliente, telefone);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<TelefoneDTO> atualizarTelefone(@PathVariable Long id, @RequestBody TelefoneDTO telefone) {
		return ResponseEntity.status(HttpStatus.CREATED).body(telefoneServico.atualizar(id, telefone));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirTelefone(@PathVariable Long id) {
		telefoneServico.excluir(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
