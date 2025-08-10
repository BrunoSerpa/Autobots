package com.autobots.automanager.validar;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;

@Component
public class ClienteValidar implements Validar<ClienteDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;
    private DocumentoValidar validarDocumento;
    private EnderecoValidar validarEndereco;
    private TelefoneValidar validarTelefone;

    public ClienteValidar(List<String> erros,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone) {
        this.erros = erros;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
    }

    @Override
    public List<String> verificar(ClienteDTO entity) {
        erros.clear();
        Map<String, Supplier<String>> campos = Map.of(
                "Nome", entity::getNome,
                "Nome Social", entity::getNomeSocial);

        campos.forEach((nome, fornecedor) -> {
            String valor = fornecedor.get();
            if (NULO.verificar(valor)) {
                erros.add("- " + nome + ";");
            }
        });

        if (entity.getDataNascimento() == null) {
            erros.add("- Data Nascimento;");
        } else if (NULO.verificar(entity.getDataNascimento().toString())) {
            erros.add("- Data Nascimento;");
        }

        if (entity.getDataCadastro() == null) {
            erros.add("- Data Cadastro;");
        } else if (NULO.verificar(entity.getDataCadastro().toString())) {
            erros.add("- Data Cadastro;");
        }

        List<String> errosEndereco = validarEndereco.verificar(entity.getEndereco());
        if (!errosEndereco.isEmpty()) {
            erros.add("- Endereco:");
            errosEndereco.forEach(erro -> erros.add(" " + erro));
        }

        for (int index = 0; index < entity.getTelefones().size(); index++) {
            List<String> errosTelefone = validarTelefone.verificar(entity.getTelefones().get(index));
            if (!errosTelefone.isEmpty()) {
                erros.add("- " + (index + 1) + "º Telefone:");
                errosTelefone.forEach(erro -> erros.add(" " + erro));
            }
        }
        for (int index = 0; index < entity.getDocumentos().size(); index++) {
            List<String> errosTelefone = validarDocumento.verificar(entity.getDocumentos().get(index));
            if (!errosTelefone.isEmpty()) {
                erros.add("- " + (index + 1) + "º Documento:");
                errosTelefone.forEach(erro -> erros.add(" " + erro));
            }
        }

        return erros;
    }
}
