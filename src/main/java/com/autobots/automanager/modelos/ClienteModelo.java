package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.dto.ClienteDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClienteModelo
                implements RepresentationModelAssembler<ClienteDTO, EntityModel<ClienteDTO>> {

        @Override
        public @NonNull EntityModel<ClienteDTO> toModel(@NonNull ClienteDTO dto) {
                Long id = dto.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(ClienteControle.class)
                                                .buscarPorId(id))
                                                .withSelfRel(),
                                linkTo(methodOn(ClienteControle.class)
                                                .listarTodos())
                                                .withRel("clientes"),
                                linkTo(methodOn(ClienteControle.class)
                                                .atualizar(id, dto))
                                                .withRel("editar")
                                                .withType("PUT"),
                                linkTo(methodOn(ClienteControle.class)
                                                .excluir(id))
                                                .withRel("excluir")
                                                .withType("DELETE"));
        }
}
