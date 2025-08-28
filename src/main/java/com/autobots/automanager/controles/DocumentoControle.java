package com.autobots.automanager.controles;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.modelos.DocumentoModelo;
import com.autobots.automanager.servicos.DocumentoServico;

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
@RequestMapping("/documento")
@Validated
@Tag(name = "Documento", description = "Operações CRUD de documentos")
@RequiredArgsConstructor
public class DocumentoControle {
	private final DocumentoServico servico;
	private final DocumentoModelo modelo;

	@Operation(summary = "Listar todos os documentos")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de documentos retornada com sucesso"),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("todos")
	public CollectionModel<EntityModel<DocumentoDTO>> listarTodos() {
		List<EntityModel<DocumentoDTO>> modelos = servico.todos().stream()
				.map(modelo::toModel)
				.toList();

		return CollectionModel.of(modelos,
				linkTo(methodOn(DocumentoControle.class).listarTodos()).withSelfRel(),
				linkTo(methodOn(DocumentoControle.class).cadastrar(null, null))
						.withRel("cadastrar")
						.withType("POST"));
	}

	@Operation(summary = "Buscar documento por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Documento encontrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@GetMapping("buscar/{id}")
	public ResponseEntity<EntityModel<DocumentoDTO>> buscarPorId(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		DocumentoDTO encontrado = servico.procurar(id);

		EntityModel<DocumentoDTO> model = modelo.toModel(encontrado);

		URI localizacao = linkTo(methodOn(DocumentoControle.class)
				.buscarPorId(encontrado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Cadastrar um novo documento")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Documento cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Problemas nos dados do documento ou ID do Usuario inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PostMapping("cadastrar/{idUsuario}")
	public ResponseEntity<EntityModel<DocumentoDTO>> cadastrar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long idUsuario,
			@Valid @RequestBody DocumentoDTO documento) {
		DocumentoDTO criado = servico.cadastro(idUsuario, documento);

		EntityModel<DocumentoDTO> model = modelo.toModel(criado);

		URI localizacao = linkTo(methodOn(DocumentoControle.class)
				.buscarPorId(criado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Atualizar um documento existente")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do documento ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@PutMapping("atualizar/{id}")
	public ResponseEntity<EntityModel<DocumentoDTO>> atualizar(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id,
			@Valid @RequestBody DocumentoDTO documento) {
		documento.setId(id);

		DocumentoDTO atualizado = servico.atualizar(documento);

		EntityModel<DocumentoDTO> model = modelo.toModel(atualizado);

		URI localizacao = linkTo(methodOn(DocumentoControle.class)
				.buscarPorId(atualizado.getId())).toUri();

		return ResponseEntity
				.created(localizacao)
				.header(HttpHeaders.LOCATION, localizacao.toString())
				.body(model);
	}

	@Operation(summary = "Excluir documento por ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Documento excluído com sucesso"),
			@ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "404", description = "Documento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
			@ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
	})
	@DeleteMapping("excluir/{id}")
	public ResponseEntity<CollectionModel<Void>> excluir(
			@PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
		servico.excluir(id);

		CollectionModel<Void> links = CollectionModel.empty();
		links.add(linkTo(methodOn(DocumentoControle.class).listarTodos()).withRel("documentos"));
		links.add(linkTo(methodOn(DocumentoControle.class).cadastrar(null,null)).withRel("cadastrar")
						.withType("POST"));

		return ResponseEntity.ok(links);
	}
}
