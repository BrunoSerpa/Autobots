package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.EmailDTO;
import com.autobots.automanager.repositorios.EmailRepositorio;

@Component
public class EmailValidar implements Validar<EmailDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final EmailRepositorio repositorio;

    public EmailValidar(EmailRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(EmailDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Email não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getEndereco())) {
            erros.add("- Endereço de email não informado;");
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<EmailDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (EmailDTO email : entities) {
            List<String> erroEmail = verificar(email);
            if (!erroEmail.isEmpty()) {
                erros.add("- " + index + "º Email:");
                erros.addAll(erroEmail);
            }
            index++;
        }

        return erros;
    }
}
