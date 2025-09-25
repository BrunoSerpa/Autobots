package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.ServicoDTO;
import com.autobots.automanager.repositorios.ServicoRepositorio;

@Component
public class ServicoValidar implements Validar<ServicoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final ServicoRepositorio repositorio;

    public ServicoValidar(ServicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(ServicoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Serviço não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getNome())) {
            erros.add("- Nome do serviço não informado;");
        }

        if (entity.getValor() <= 0) {
            erros.add("- Valor do serviço deve ser maior que zero;");
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<ServicoDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (ServicoDTO servico : entities) {
            List<String> erroServico = verificar(servico);
            if (!erroServico.isEmpty()) {
                erros.add("- " + index + "º Serviço:");
                erros.addAll(erroServico);
            }
            index++;
        }

        return erros;
    }
}
