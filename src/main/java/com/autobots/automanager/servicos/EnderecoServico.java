package com.autobots.automanager.servicos;

import com.autobots.automanager.atualizar.EnderecoAtualizador;
import com.autobots.automanager.converter.EnderecoConverter;
import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.validar.EnderecoValidar;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EnderecoServico {
	private static final String NAO_ENCONTRADO = "Endereço não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String ENDERECO_EXISTENTE = "Usuario já possui endereço.";
	private static final String ERRO_ENCONTRADO = "Problemas no endereço:";

	private final UsuarioServico servicoUsuario;
	private final EnderecoAtualizador atualizador;
	private final EnderecoConverter conversor;
	private final EnderecoRepositorio repositorio;
	private final UsuarioRepositorio repositorioUsuario;
	private final EnderecoValidar validar;

	public EnderecoServico(UsuarioServico servicoUsuario,
			EnderecoAtualizador atualizador,
			EnderecoConverter conversor,
			EnderecoRepositorio repositorio,
			UsuarioRepositorio repositorioUsuario,
			EnderecoValidar validar) {
		this.servicoUsuario = servicoUsuario;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioUsuario = repositorioUsuario;
		this.validar = validar;
	}

	public EnderecoDTO procurar(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<EnderecoDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public EnderecoDTO cadastro(Long idUsuario, @Valid EnderecoDTO dto) {
		if (idUsuario == null || idUsuario <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		UsuarioDTO cliente = servicoUsuario.procurar(idUsuario);
		if (cliente.getEndereco() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ENDERECO_EXISTENTE);
		}

		cliente.setEndereco(dto);
		servicoUsuario.atualizar(cliente);
		cliente = servicoUsuario.procurar(idUsuario);

		EnderecoDTO criado = cliente.getEndereco();
		return criado;
	}

	public EnderecoDTO atualizar(@Valid EnderecoDTO dto) {
		Long id = dto.getId();
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Endereco existente = repositorio.findById(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(dto));
		Endereco salvo = repositorio.save(existente);
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Usuario cliente = repositorioUsuario.findOneByEnderecoId(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.setEndereco(null);
		repositorioUsuario.save(cliente);
	}
}
