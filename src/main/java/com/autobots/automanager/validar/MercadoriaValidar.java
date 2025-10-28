package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.MercadoriaDTO;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;

@Component
public class MercadoriaValidar implements Validar<MercadoriaDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final MercadoriaRepositorio repositorio;

    public MercadoriaValidar(MercadoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(MercadoriaDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Mercadoria não cadastrada");
            }
            return erros;
        }

        if (entity.getValidade() == null) {
            erros.add("- Data de validade não informada");
        }

        if (entity.getFabricao() == null) {
            erros.add("- Data de fabricação não informada");
        }

        if (NULO.verificar(entity.getNome())) {
            erros.add("- Nome da mercadoria não informado");
        }

        if (entity.getQuantidade() <= 0) {
            erros.add("- Quantidade deve ser maior que zero");
        }

        if (entity.getValor() <= 0) {
            erros.add("- Valor deve ser maior que zero");
        }

        if (entity.getFabricao() != null && entity.getValidade() != null
            && entity.getValidade().before(entity.getFabricao())) {
            erros.add("- Data de validade anterior à data de fabricação");
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<MercadoriaDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (MercadoriaDTO mercadoria : entities) {
            List<String> erroMercadoria = verificar(mercadoria);
            if (!erroMercadoria.isEmpty()) {
                erros.add("- " + index + "ª Mercadoria:");
                erros.addAll(erroMercadoria);
            }
            index++;
        }

        return erros;
    }
}
