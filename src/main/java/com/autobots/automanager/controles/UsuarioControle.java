package com.autobots.automanager.controles;

import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.modelos.UsuarioModelo;
import com.autobots.automanager.servicos.UsuarioServico;

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
@RequestMapping("/usuario")
@Validated
@Tag(name = "Usuário", description = "Operações CRUD de usuários")
@RequiredArgsConstructor
public class UsuarioControle {
        private final UsuarioServico servico;
        private final UsuarioModelo modelo;

        @Operation(summary = "Listar todos os usuários")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping("todos")
        public CollectionModel<EntityModel<UsuarioDTO>> listarTodos() {
                List<EntityModel<UsuarioDTO>> lista = servico.todos().stream()
                                .map(modelo::toModel)
                                .toList();

                return CollectionModel.of(lista,
                                linkTo(methodOn(UsuarioControle.class).listarTodos()).withSelfRel(),
                                linkTo(methodOn(UsuarioControle.class).cadastrar(null))
                                                .withRel("cadastrar")
                                                .withType("POST"));
        }

        @Operation(summary = "Buscar usuário por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping("buscar/{id}")
        public ResponseEntity<EntityModel<UsuarioDTO>> buscarPorId(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                UsuarioDTO encontrado = servico.procurar(id);

                EntityModel<UsuarioDTO> model = modelo.toModel(encontrado);

                URI localizacao = linkTo(methodOn(UsuarioControle.class)
                                .buscarPorId(encontrado.getId())).toUri();

                return ResponseEntity
                                .created(localizacao)
                                .header(HttpHeaders.LOCATION, localizacao.toString())
                                .body(model);
        }

        @Operation(summary = "Cadastrar um novo usuário")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Problemas nos dados do usuário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PostMapping("cadastrar")
        public ResponseEntity<EntityModel<UsuarioDTO>> cadastrar(
                        @Valid @RequestBody UsuarioDTO clienteDto) {
                UsuarioDTO criado = servico.cadastro(clienteDto);

                EntityModel<UsuarioDTO> model = modelo.toModel(criado);

                URI localizacao = linkTo(methodOn(UsuarioControle.class)
                                .buscarPorId(criado.getId())).toUri();

                return ResponseEntity
                                .created(localizacao)
                                .header(HttpHeaders.LOCATION, localizacao.toString())
                                .body(model);
        }

        @Operation(summary = "Atualizar um cliente existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = UsuarioDTO.class))),
                        @ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do usuário", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PutMapping("atualizar/{id}")
        public ResponseEntity<EntityModel<UsuarioDTO>> atualizar(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id,
                        @Valid @RequestBody UsuarioDTO clienteDto) {
                clienteDto.setId(id);

                UsuarioDTO atualizado = servico.atualizar(clienteDto);

                EntityModel<UsuarioDTO> model = modelo.toModel(atualizado);

                URI localizacao = linkTo(methodOn(UsuarioControle.class)
                                .buscarPorId(atualizado.getId())).toUri();

                return ResponseEntity
                                .created(localizacao)
                                .header(HttpHeaders.LOCATION, localizacao.toString())
                                .body(model);
        }

        @Operation(summary = "Excluir usuário por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @DeleteMapping("excluir/{id}")
        public ResponseEntity<CollectionModel<Void>> excluir(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                servico.excluir(id);

                CollectionModel<Void> links = CollectionModel.empty();
                links.add(linkTo(methodOn(UsuarioControle.class).listarTodos()).withRel("usuários"));
                links.add(linkTo(methodOn(UsuarioControle.class).cadastrar(null)).withRel("cadastrar")
                                .withType("POST"));

                return ResponseEntity.ok(links);
        }
}
