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

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.servicos.DocumentoServico;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {
	private DocumentoServico documentoServico;

	public DocumentoControle(DocumentoServico documentoServico) {
		this.documentoServico = documentoServico;
	}

	@GetMapping("/{id}")
	public ResponseEntity<DocumentoDTO> procurarDocumento(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(documentoServico.procurar(id));
	}

	@GetMapping
	public ResponseEntity<List<DocumentoDTO>> todosDocumentos() {
		return ResponseEntity.status(HttpStatus.OK).body(documentoServico.todos());
	}

	@PostMapping("/{idCliente}")
	public ResponseEntity<Void> cadastroDocumento(@PathVariable Long idCliente, @RequestBody DocumentoDTO documento) {
		documentoServico.cadastro(idCliente, documento);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping
	public ResponseEntity<DocumentoDTO> atualizarDocumento(@RequestBody DocumentoDTO documento) {
		return ResponseEntity.status(HttpStatus.CREATED).body(documentoServico.atualizar(documento));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirDocumento(@PathVariable Long id) {
		documentoServico.excluir(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
