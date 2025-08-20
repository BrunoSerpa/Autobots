package com.autobots.automanager.controles;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.servicos.EnderecoServico;
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
@RequestMapping("/endereco")
@Validated
@Tag(name = "Endereço", description = "Operações CRUD de endereços")
@RequiredArgsConstructor
public class EnderecoControle {
	private final EnderecoServico servico;

	@Operation(summary = "Listar todos os endereços")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping
	public ResponseEntity<List<EnderecoDTO>> listarTodos() {
		return ResponseEntity.ok(servico.todos());
	}

	@Operation(summary = "Buscar endereço por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("/{id}")
	public ResponseEntity<EnderecoDTO> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		EnderecoDTO dto = servico.procurar(id);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Cadastrar um novo endereço para um cliente")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Problemas nos Dados ou cliente já possui endereço", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("/{idCliente}")
	public ResponseEntity<EnderecoDTO> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idCliente,
			@Valid @RequestBody EnderecoDTO endereco) {
		EnderecoDTO criado = servico.cadastro(idCliente, endereco);
		URI location = URI.create("/endereco/" + criado.getId());
		return ResponseEntity
				.created(location)
				.header(HttpHeaders.LOCATION, location.toString())
				.body(criado);
	}

	@Operation(summary = "Atualizar um endereço existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do endereço", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping
	public ResponseEntity<EnderecoDTO> atualizar(
			@Valid @RequestBody EnderecoDTO endereco) {
		EnderecoDTO atualizado = servico.atualizar(endereco);
		return ResponseEntity.ok(atualizado);
	}

	@Operation(summary = "Excluir endereço por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Endereço excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
