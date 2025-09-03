package com.autobots.automanager.dto;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendaDTO {
    private Long id;
    private Date cadastro;
    private String identificacao;
    private UsuarioDTO cliente;
    private UsuarioDTO funcionario;
    private Set<MercadoriaDTO> mercadorias;
    private Set<ServicoDTO> servicos;
    private VeiculoDTO veiculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCadastro() {
        return cadastro;
    }
    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public UsuarioDTO getCliente() {
        return cliente;
    }

    public void setCliente(UsuarioDTO cliente) {
        this.cliente = cliente;
    }

    public UsuarioDTO getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(UsuarioDTO funcionario) {
        this.funcionario = funcionario;
    }

    public Set<MercadoriaDTO> getMercadorias() {
        return mercadorias;
    }

    public void setMercadorias(Set<MercadoriaDTO> mercadorias) {
        this.mercadorias = mercadorias;
    }

    public Set<ServicoDTO> getServicos() {
        return servicos;
    }

    public void setServicos(Set<ServicoDTO> servicos) {
        this.servicos = servicos;
    }

    public VeiculoDTO getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoDTO veiculo) {
        this.veiculo = veiculo;
    }
}
