package com.autobots.automanager.controles;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.servicos.ClienteServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/cliente")
@Validated
@Tag(name = "Cliente", description = "Operações CRUD de clientes")
@RequiredArgsConstructor
public class ClienteControle {
        private final ClienteServico servico;

        @Operation(summary = "Listar todos os clientes")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping
        public ResponseEntity<List<ClienteDTO>> listarTodos() {
                return ResponseEntity.ok(servico.todos());
        }

        @Operation(summary = "Buscar cliente por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<ClienteDTO> buscarPorId(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                return ResponseEntity.ok(servico.procurar(id));
        }

        @Operation(summary = "Cadastrar um novo cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Problemas nos dados do cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PostMapping
        public ResponseEntity<ClienteDTO> cadastrar(@Valid @RequestBody ClienteDTO cliente) {
                ClienteDTO criado = servico.cadastro(cliente);
                URI location = URI.create("/cliente/" + criado.getId());
                return ResponseEntity
                                .created(location)
                                .header(HttpHeaders.LOCATION, location.toString())
                                .body(criado);
        }

        @Operation(summary = "Atualizar um cliente existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID inválido ou problemas nos dados do cliente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @PutMapping
        public ResponseEntity<ClienteDTO> atualizar(@Valid @RequestBody ClienteDTO cliente) {
                return ResponseEntity.ok(servico.atualizar(cliente));
        }

        @Operation(summary = "Excluir cliente por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
                        @ApiResponse(responseCode = "400", description = "ID não informado ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class))),
                        @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroControle.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> excluir(
                        @PathVariable @Positive(message = "ID deve ser um número positivo") Long id) {
                servico.excluir(id);
                return ResponseEntity.noContent().build();
        }
}
