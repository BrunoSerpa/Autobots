package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Component
public class DocumentoValidar implements Validar<DocumentoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private DocumentoRepositorio repositorio;

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

        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Sem Número;");
        } else if (repositorio.findByNumero(entity.getNumero()).isPresent()) {
            erros.add("- Documento já cadastrado;");
        }

        return erros;
    }

    @Override
    public List<String> verificar(List<DocumentoDTO> entities) {
        List<String> erros = new ArrayList<>();

        for (int index = 0; entities.size() > index; index++) {
            List<String> erroDocumento = verificar(entities.get(index));
            if (!erroDocumento.isEmpty()) {
                erros.add("- " + (index + 1) + "º Documento:");
                erros.addAll(erroDocumento);
            }
        }

        return erros;
    }
}
