package com.autobots.automanager.controles;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.modelos.ClienteModelo;
import com.autobots.automanager.servicos.ClienteServico;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/cliente")
@Validated
@Tag(name = "Cliente", description = "Operações CRUD de clientes")
@RequiredArgsConstructor
public class ClienteControle {
        private final ClienteServico servico;
        private final ClienteModelo modelo;

        @Operation(summary = "Listar todos os clientes")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping
        public CollectionModel<EntityModel<ClienteDTO>> listarTodos() {
                List<EntityModel<ClienteDTO>> lista = servico.todos().stream()
                                .map(modelo::toModel)
                                .toList();

                return CollectionModel.of(lista,
                                linkTo(methodOn(ClienteControle.class)
                                                .listarTodos()).withSelfRel(),
                                linkTo(methodOn(ClienteControle.class)
                                                .cadastrar(null)).withRel("cadastrar").withType("POST"));
        }

        @Operation(summary = "Buscar cliente por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping("/{id}")
        public EntityModel<ClienteDTO> buscarPorId(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                ClienteDTO dto = servico.procurar(id);
                return modelo.toModel(dto);
        }

        @Operation(summary = "Cadastrar um novo cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Problemas nos dados do cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PostMapping
        public ResponseEntity<EntityModel<ClienteDTO>> cadastrar(
                        @Valid @RequestBody ClienteDTO clienteDto) {

                ClienteDTO criado = servico.cadastro(clienteDto);
                EntityModel<ClienteDTO> model = modelo.toModel(criado);

                URI location = linkTo(methodOn(ClienteControle.class)
                                .buscarPorId(criado.getId())).toUri();

                return ResponseEntity
                                .created(location)
                                .header(HttpHeaders.LOCATION, location.toString())
                                .body(model);
        }

        @Operation(summary = "Atualizar um cliente existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PutMapping("/{id}")
        public EntityModel<ClienteDTO> atualizar(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id,
                        @Valid @RequestBody ClienteDTO clienteDto) {

                clienteDto.setId(id);
                ClienteDTO atualizado = servico.atualizar(clienteDto);
                return modelo.toModel(atualizado);
        }

        @Operation(summary = "Excluir cliente por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public ResponseEntity<Void> excluir(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                servico.excluir(id);
                return ResponseEntity.noContent().build();
        }
}