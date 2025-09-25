package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Component
public class DocumentoValidar implements Validar<DocumentoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final DocumentoRepositorio repositorio;

    public DocumentoValidar(DocumentoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(DocumentoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Documento não cadastrado;");
            }
            return erros;
        }

        if (entity.getTipo() == null) {
            erros.add("- Tipo de documento não informado;");
        }

        if (entity.getDataEmissao() == null) {
            erros.add("- Data de emissão não informada;");
        }

        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Número do documento não informado;");
        } else {
            repositorio.findByNumero(entity.getNumero())
                    .ifPresent(doc -> erros.add("- Documento já cadastrado;"));
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<DocumentoDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (DocumentoDTO documento : entities) {
            List<String> erroDocumento = verificar(documento);
            if (!erroDocumento.isEmpty()) {
                erros.add("- " + index + "º Documento:");
                erros.addAll(erroDocumento);
            }
            index++;
        }

        return erros;
    }
}
