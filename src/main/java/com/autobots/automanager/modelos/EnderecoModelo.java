package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.dto.EnderecoDTO;

import org.springframework.lang.NonNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EnderecoModelo
                implements RepresentationModelAssembler<EnderecoDTO, EntityModel<EnderecoDTO>> {

        @Override
        public @NonNull EntityModel<EnderecoDTO> toModel(@NonNull EnderecoDTO dto) {
                Long id = dto.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(EnderecoControle.class)
                                                .buscarPorId(id))
                                                .withSelfRel(),
                                linkTo(methodOn(EnderecoControle.class)
                                                .listarTodos())
                                                .withRel("endereços"),
                                linkTo(methodOn(EnderecoControle.class)
                                                .atualizar(id, dto))
                                                .withRel("editar")
                                                .withType("PUT"),
                                linkTo(methodOn(EnderecoControle.class)
                                                .excluir(id))
                                                .withRel("excluir")
                                                .withType("DELETE"));
        }
}