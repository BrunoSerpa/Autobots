package com.autobots.automanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpresaDTO {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private Set<TelefoneDTO> telefones;
    private EnderecoDTO endereco;
    private Date cadastro;
    private Set<UsuarioDTO> usuarios;
    private Set<MercadoriaDTO> mercadorias;
    private Set<ServicoDTO> servicos;
    private Set<VendaDTO> vendas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public Set<TelefoneDTO> getTelefones() {
        return telefones;
    }

    public void setTelefones(Set<TelefoneDTO> telefones) {
        this.telefones = telefones;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    public Date getCadastro() {
        return cadastro;
    }

    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }

    public Set<UsuarioDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<UsuarioDTO> usuarios) {
        this.usuarios = usuarios;
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

    public Set<VendaDTO> getVendas() {
        return vendas;
    }

    public void setVendas(Set<VendaDTO> vendas) {
        this.vendas = vendas;
    }
}
