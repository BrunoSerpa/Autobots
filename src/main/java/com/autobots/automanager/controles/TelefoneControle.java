package com.autobots.automanager.controles;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.modelos.TelefoneModelo;
import com.autobots.automanager.servicos.TelefoneServico;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

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
@RequestMapping("/telefone")
@Validated
@Tag(name = "Telefone", description = "Operações CRUD de telefones")
@RequiredArgsConstructor
public class TelefoneControle {
	private final TelefoneServico servico;
	private final TelefoneModelo modelo;

	@Operation(summary = "Listar todos os telefones")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de telefones retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("todos")
	public CollectionModel<EntityModel<TelefoneDTO>> listarTodos() {
		List<EntityModel<TelefoneDTO>> lista = servico.todos().stream()
				.map(modelo::toModel)
				.toList();

		return CollectionModel.of(lista,
				linkTo(methodOn(TelefoneControle.class).listarTodos()).withSelfRel(),
				linkTo(methodOn(TelefoneControle.class).cadastrar(null,null))
						.withRel("cadastrar")
						.withType("POST"));
	}

	@Operation(summary = "Buscar telefone por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Telefone encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("buscar/{id}")
	public ResponseEntity<EntityModel<TelefoneDTO>> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		TelefoneDTO encontrado = servico.procurar(id);

		EntityModel<TelefoneDTO> model = modelo.toModel(encontrado);

		URI localizacao = linkTo(methodOn(TelefoneControle.class)
				.buscarPorId(encontrado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Cadastrar um novo telefone para um cliente")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Telefone cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("cadastrar/{idCliente}")
	public ResponseEntity<EntityModel<TelefoneDTO>> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idCliente,
			@Valid @RequestBody TelefoneDTO telefoneDto) {
		TelefoneDTO criado = servico.cadastro(idCliente, telefoneDto);

		EntityModel<TelefoneDTO> model = modelo.toModel(criado);

		URI location = linkTo(methodOn(TelefoneControle.class)
				.buscarPorId(criado.getId())).toUri();

		return ResponseEntity
				.created(location)
				.header(HttpHeaders.LOCATION, location.toString())
				.body(model);
	}

	@Operation(summary = "Atualizar um telefone existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Telefone atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados do telefone inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping("atualizar/{id}")
	public ResponseEntity<EntityModel<TelefoneDTO>> atualizar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id,
			@Valid @RequestBody TelefoneDTO telefone) {
		telefone.setId(id);

		TelefoneDTO atualizado = servico.atualizar(telefone);

		EntityModel<TelefoneDTO> model = modelo.toModel(atualizado);

		URI localizacao = linkTo(methodOn(TelefoneControle.class)
				.buscarPorId(atualizado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Excluir telefone por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Telefone excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("excluir/{id}")
	public ResponseEntity<CollectionModel<Void>> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);

		CollectionModel<Void> links = CollectionModel.empty();
		links.add(linkTo(methodOn(DocumentoControle.class).listarTodos()).withRel("telefones"));
		links.add(linkTo(methodOn(DocumentoControle.class).cadastrar(null, null)).withRel("cadastrar")
				.withType("POST"));

		return ResponseEntity.ok(links);
	}
}
