package com.autobots.automanager.dto;

import com.autobots.automanager.enumeracoes.PerfilUsuario;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private Long id;
	private String nome;
	private String nomeSocial;
	private Set<PerfilUsuario> perfis;
	private Set<DocumentoDTO> documentos;
	private EnderecoDTO endereco;
	private Set<TelefoneDTO> telefones;
	private Set<EmailDTO> emails;
	private Set<CredencialDTO> credenciais;
	private Set<MercadoriaDTO> mercadorias;
	private Set<VendaDTO> vendas;
	private Set<VeiculoDTO> veiculos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public Set<PerfilUsuario> getPerfis() {
		return perfis;
	}

	public void setPerfis(Set<PerfilUsuario> perfis) {
		this.perfis = perfis;
	}

	public Set<DocumentoDTO> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Set<DocumentoDTO> documentos) {
		this.documentos = documentos;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}

	public Set<TelefoneDTO> getTelefones() {
		return telefones;
	}

	public void setTelefones(Set<TelefoneDTO> telefones) {
		this.telefones = telefones;
	}

	public Set<EmailDTO> getEmails() {
		return emails;
	}

	public void setEmails(Set<EmailDTO> emails) {
		this.emails = emails;
	}

	public Set<CredencialDTO> getCredenciais() {
		return credenciais;
	}

	public void setCredenciais(Set<CredencialDTO> credenciais) {
		this.credenciais = credenciais;
	}

	public Set<MercadoriaDTO> getMercadorias() {
		return mercadorias;
	}

	public void setMercadorias(Set<MercadoriaDTO> mercadorias) {
		this.mercadorias = mercadorias;
	}

	public Set<VendaDTO> getVendas() {
		return vendas;
	}

	public void setVendas(Set<VendaDTO> vendas) {
		this.vendas = vendas;
	}

	public Set<VeiculoDTO> getVeiculos() {
		return veiculos;
	}

	public void setVeiculos(Set<VeiculoDTO> veiculos) {
		this.veiculos = veiculos;
	}
}
