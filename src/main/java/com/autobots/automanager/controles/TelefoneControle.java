package com.autobots.automanager.controles;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.servicos.TelefoneServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/telefone")
@Validated
@Tag(name = "Telefone", description = "Operações CRUD de telefones")
@RequiredArgsConstructor
public class TelefoneControle {
	private final TelefoneServico servico;

	@Operation(summary = "Listar todos os telefones")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de telefones retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping
	public ResponseEntity<List<TelefoneDTO>> listarTodos() {
		return ResponseEntity.ok(servico.todos());
	}

	@Operation(summary = "Buscar telefone por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Telefone encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("/{id}")
	public ResponseEntity<TelefoneDTO> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		return ResponseEntity.ok(servico.procurar(id));
	}

	@Operation(summary = "Cadastrar um novo telefone para um cliente")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Telefone cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("/{idCliente}")
	public ResponseEntity<TelefoneDTO> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idCliente,
			@Valid @RequestBody TelefoneDTO telefone) {
		TelefoneDTO criado = servico.cadastro(idCliente, telefone);
		URI location = URI.create("/telefone/" + criado.getId());
		return ResponseEntity
				.created(location)
				.header(HttpHeaders.LOCATION, location.toString())
				.body(criado);
	}

	@Operation(summary = "Atualizar um telefone existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Telefone atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados do telefone inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping
	public ResponseEntity<TelefoneDTO> atualizar(
			@Valid @RequestBody TelefoneDTO telefone) {
		return ResponseEntity.ok(servico.atualizar(telefone));
	}

	@Operation(summary = "Excluir telefone por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Telefone excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
