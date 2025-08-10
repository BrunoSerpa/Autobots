package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.EnderecoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.validar.EnderecoValidar;

@Service
public class EnderecoServico {
	private static final String NAO_ENCONTRADO = "Endereço não encontrado";
	private static final String ENDERECO_EXISTENTE = "Cliente possui endereço";

	private ClienteServico servicoCliente;
	private EnderecoAtualizador atualizador;
	private EnderecoConverter conversor;
	private EnderecoRepositorio repositorio;
	private EnderecoValidar validar;

	public EnderecoServico(ClienteServico servicoCliente,
			EnderecoAtualizador atualizador,
			EnderecoConverter conversor,
			EnderecoRepositorio repositorio,
			EnderecoValidar validar) {
		this.servicoCliente = servicoCliente;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.validar = validar;
	}

	public EnderecoDTO procurar(Long id) {
		Endereco endereco = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversor.convertToDto(endereco);
	}

	public List<EnderecoDTO> todos() {
		List<Endereco> enderecos = repositorio.findAll();
		return conversor.convertToDto(enderecos);
	}

	public EnderecoDTO cadastro(Long idCliente, EnderecoDTO enderecoDTO) {
		List<String> erros = validar.verificar(enderecoDTO);
		if (!erros.isEmpty()) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falta os dados:");
			erros.forEach(erro -> mensagem.append("\n").append(erro));
			throw new IllegalArgumentException(mensagem.toString());
		}
		
		ClienteDTO cliente = servicoCliente.procurar(idCliente);
		if (cliente.getEndereco() != null) {
			throw new IllegalArgumentException(ENDERECO_EXISTENTE);
		}

		cliente.setEndereco(enderecoDTO);
		servicoCliente.atualizar(idCliente, cliente);
		return cliente.getEndereco();
	}

	public EnderecoDTO atualizar(Long id, EnderecoDTO enderecoDTO) {
		Endereco endereco = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		atualizador.atualizar(endereco, conversor.convertToEntity(enderecoDTO));
		return conversor.convertToDto(endereco);
	}

	public void excluir(Long idCliente) {
		ClienteDTO cliente = servicoCliente.procurar(idCliente);
		if (cliente.getEndereco() == null) {
			throw new IllegalArgumentException(NAO_ENCONTRADO);
		}
		cliente.setEndereco(null);
		servicoCliente.atualizar(idCliente, cliente);
	}
}
