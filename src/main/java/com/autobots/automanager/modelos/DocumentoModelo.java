package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.dto.DocumentoDTO;

import org.springframework.lang.NonNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DocumentoModelo
                implements RepresentationModelAssembler<DocumentoDTO, EntityModel<DocumentoDTO>> {

        @Override
        public @NonNull EntityModel<DocumentoDTO> toModel(@NonNull DocumentoDTO dto) {
                Long id = dto.getId();

                return EntityModel.of(dto,
                                linkTo(methodOn(DocumentoControle.class)
                                                .buscarPorId(id))
                                                .withSelfRel(),
                                linkTo(methodOn(DocumentoControle.class)
                                                .listarTodos())
                                                .withRel("documentos"),
                                linkTo(methodOn(DocumentoControle.class)
                                                .atualizar(id, dto))
                                                .withRel("editar")
                                                .withType("PUT"),
                                linkTo(methodOn(DocumentoControle.class)
                                                .excluir(id))
                                                .withRel("excluir")
                                                .withType("DELETE"));
        }
}