package com.autobots.automanager.controles;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.modelos.EnderecoModelo;
import com.autobots.automanager.servicos.EnderecoServico;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/endereco")
@Validated
@Tag(name = "Endereço", description = "Operações CRUD de endereços")
@RequiredArgsConstructor
public class EnderecoControle {
	private final EnderecoServico servico;
	private final EnderecoModelo modelo;

	@Operation(summary = "Listar todos os endereços")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("todos")
	public CollectionModel<EntityModel<EnderecoDTO>> listarTodos() {
		List<EntityModel<EnderecoDTO>> modelos = servico.todos().stream()
				.map(modelo::toModel)
				.toList();

		return CollectionModel.of(modelos,
				linkTo(methodOn(EnderecoControle.class).listarTodos()).withSelfRel(),
				linkTo(methodOn(EnderecoControle.class).cadastrar(null, null))
						.withRel("cadastrar")
						.withType("POST"));
	}

	@Operation(summary = "Buscar endereço por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("buscar/{id}")
	public ResponseEntity<EntityModel<EnderecoDTO>> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		EnderecoDTO encontrado = servico.procurar(id);

		EntityModel<EnderecoDTO> model = modelo.toModel(encontrado);

		URI localizacao = linkTo(methodOn(EnderecoControle.class)
				.buscarPorId(encontrado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Cadastrar um novo endereço")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Problemas nos Dados ou cliente já possui endereço", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("cadastrar/{idCliente}")
	public ResponseEntity<EntityModel<EnderecoDTO>> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idCliente,
			@Valid @RequestBody EnderecoDTO endereco) {
		EnderecoDTO criado = servico.cadastro(idCliente, endereco);

		EntityModel<EnderecoDTO> model = modelo.toModel(criado);

		URI localizacao = linkTo(methodOn(EnderecoControle.class)
				.buscarPorId(criado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Atualizar um endereço existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do endereço", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping("atualizar/{id}")
	public ResponseEntity<EntityModel<EnderecoDTO>> atualizar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id,
			@Valid @RequestBody EnderecoDTO endereco) {
		endereco.setId(id);

		EnderecoDTO atualizado = servico.atualizar(endereco);

		EntityModel<EnderecoDTO> model = modelo.toModel(atualizado);

		URI localizacao = linkTo(methodOn(EnderecoControle.class)
				.buscarPorId(atualizado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Excluir endereço por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Endereço excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("excluir/{id}")
	public ResponseEntity<CollectionModel<Void>> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);

		CollectionModel<Void> links = CollectionModel.empty();
		links.add(linkTo(methodOn(EnderecoControle.class).listarTodos()).withRel("endereços"));
		links.add(linkTo(methodOn(EnderecoControle.class).cadastrar(null,null)).withRel("cadastrar")
						.withType("POST"));

		return ResponseEntity.ok(links);
	}
}
