package com.autobots.automanager.dto;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {
	private Long Id;
	private String nome;
	private String nomeSocial;
	private Date dataNascimento;
	private Date dataCadastro;
	private List<DocumentoDTO> documentos;
	private EnderecoDTO endereco;
	private List<TelefoneDTO> telefones;
}