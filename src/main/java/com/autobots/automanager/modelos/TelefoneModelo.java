package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.dto.TelefoneDTO;

import org.springframework.lang.NonNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TelefoneModelo
                implements RepresentationModelAssembler<TelefoneDTO, EntityModel<TelefoneDTO>> {

        @Override
        public @NonNull EntityModel<TelefoneDTO> toModel(@NonNull TelefoneDTO dto) {
                Long id = dto.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(TelefoneControle.class)
                                                .buscarPorId(id))
                                                .withSelfRel(),
                                linkTo(methodOn(TelefoneControle.class)
                                                .listarTodos())
                                                .withRel("telefones"),
                                linkTo(methodOn(TelefoneControle.class)
                                                .atualizar(id, dto))
                                                .withRel("editar")
                                                .withType("PUT"),
                                linkTo(methodOn(TelefoneControle.class)
                                                .excluir(id))
                                                .withRel("excluir")
                                                .withType("DELETE"));
        }
}