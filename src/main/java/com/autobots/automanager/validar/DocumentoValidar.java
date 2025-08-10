package com.autobots.automanager.validar;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Component
public class DocumentoValidar implements Validar<DocumentoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;
    private DocumentoRepositorio repositorio;

    public DocumentoValidar(List<String> erros,
            DocumentoRepositorio repositorio) {
        this.erros = erros;
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(DocumentoDTO entity) {
        erros.clear();

        if (NULO.verificar(entity.getTipo())) {
            erros.add("- Tipo;");
        }
        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Número;");
        } else if (repositorio.findByNumero(entity.getNumero()).isPresent()) {
            erros.add("- Documento já cadastrado;");
        }

        return erros;
    }
}
