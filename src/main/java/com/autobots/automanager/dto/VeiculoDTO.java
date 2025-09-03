package com.autobots.automanager.dto;

import com.autobots.automanager.enumeracoes.TipoVeiculo;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VeiculoDTO {
    private Long id;
    private TipoVeiculo tipo;
    private String modelo;
    private String placa;
    private UsuarioDTO proprietario;
    private Set<VendaDTO> vendas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public UsuarioDTO getProprietario() {
        return proprietario;
    }

    public void setProprietario(UsuarioDTO proprietario) {
        this.proprietario = proprietario;
    }

    public Set<VendaDTO> getVendas() {
        return vendas;
    }

    public void setVendas(Set<VendaDTO> vendas) {
        this.vendas = vendas;
    }
}
