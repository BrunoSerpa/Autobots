package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.dto.UsuarioDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelo
                implements RepresentationModelAssembler<UsuarioDTO, EntityModel<UsuarioDTO>> {

        @Override
        public @NonNull EntityModel<UsuarioDTO> toModel(@NonNull UsuarioDTO dto) {
                Long id = dto.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(UsuarioControle.class)
                                                .buscarPorId(id))
                                                .withSelfRel(),
                                linkTo(methodOn(UsuarioControle.class)
                                                .listarTodos())
                                                .withRel("clientes"),
                                linkTo(methodOn(UsuarioControle.class)
                                                .atualizar(id, dto))
                                                .withRel("editar")
                                                .withType("PUT"),
                                linkTo(methodOn(UsuarioControle.class)
                                                .excluir(id))
                                                .withRel("excluir")
                                                .withType("DELETE"));
        }
}
