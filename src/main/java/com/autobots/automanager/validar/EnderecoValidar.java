package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Component
public class EnderecoValidar implements Validar<EnderecoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final EnderecoRepositorio repositorio;

    public EnderecoValidar(EnderecoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(EnderecoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Endereço não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getEstado())) {
            erros.add("- Estado não informado;");
        }

        if (NULO.verificar(entity.getCidade())) {
            erros.add("- Cidade não informada;");
        }

        if (NULO.verificar(entity.getBairro())) {
            erros.add("- Bairro não informado;");
        }

        if (NULO.verificar(entity.getRua())) {
            erros.add("- Rua não informada;");
        }

        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Número não informado;");
        }

        if (NULO.verificar(entity.getCodigoPostal())) {
            erros.add("- Código postal não informado;");
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<EnderecoDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (EnderecoDTO endereco : entities) {
            List<String> erroEndereco = verificar(endereco);
            if (!erroEndereco.isEmpty()) {
                erros.add("- " + index + "º Endereço:");
                erros.addAll(erroEndereco);
            }
            index++;
        }

        return erros;
    }
}
