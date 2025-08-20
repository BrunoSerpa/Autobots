package com.autobots.automanager.controles;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.servicos.DocumentoServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/documento")
@Validated
@Tag(name = "Documento", description = "Operações CRUD de documentos")
@RequiredArgsConstructor
public class DocumentoControle {
	private final DocumentoServico servico;

	@Operation(summary = "Listar todos os documentos")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de documentos retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping
	public ResponseEntity<List<DocumentoDTO>> listarTodos() {
		return ResponseEntity.ok(servico.todos());
	}

	@Operation(summary = "Buscar documento por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Documento encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("/{id}")
	public ResponseEntity<DocumentoDTO> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		DocumentoDTO dto = servico.procurar(id);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Cadastrar um novo documento para um cliente")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Documento cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Problemas nos dados do documento ou ID do Cliente inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("/{idCliente}")
	public ResponseEntity<DocumentoDTO> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idCliente,
			@Valid @RequestBody DocumentoDTO documento) {
		DocumentoDTO criado = servico.cadastro(idCliente, documento);
		URI location = URI.create("/documento/" + criado.getId());
		return ResponseEntity
				.created(location)
				.header(HttpHeaders.LOCATION, location.toString())
				.body(criado);
	}

	@Operation(summary = "Atualizar um documento existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do documento ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping
	public ResponseEntity<DocumentoDTO> atualizar(
			@Valid @RequestBody DocumentoDTO documento) {
		DocumentoDTO atualizado = servico.atualizar(documento);
		return ResponseEntity.ok(atualizado);
	}

	@Operation(summary = "Excluir documento por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Documento excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
