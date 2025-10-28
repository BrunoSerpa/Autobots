package com.autobots.automanager.servicos;

import com.autobots.automanager.atualizar.TelefoneAtualizador;
import com.autobots.automanager.converter.TelefoneConverter;
import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.validar.TelefoneValidar;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.TreeSet;

@Service
public class TelefoneServico {
	private static final String NAO_ENCONTRADO = "Telefone não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String SEM_ID = "Telefone não possui ID.";
	private static final String ERRO_ENCONTRADO = "Problemas no Telefone:";

	private final UsuarioServico servicoUsuario;
	private final TelefoneAtualizador atualizador;
	private final TelefoneConverter conversor;
	private final TelefoneRepositorio repositorio;
	private final UsuarioRepositorio repositorioUsuario;
	private final TelefoneValidar validar;

	public TelefoneServico(UsuarioServico servicoUsuario,
			UsuarioRepositorio repositorioUsuario,
			TelefoneAtualizador atualizador,
			TelefoneConverter conversor,
			TelefoneRepositorio repositorio,
			TelefoneValidar validar) {
		this.servicoUsuario = servicoUsuario;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioUsuario = repositorioUsuario;
		this.validar = validar;
	}

	public TelefoneDTO procurar(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<TelefoneDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public TelefoneDTO cadastro(Long idUsuario, @Valid TelefoneDTO telefoneDTO) {
		if (idUsuario == null || idUsuario <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(telefoneDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		UsuarioDTO cliente = servicoUsuario.procurar(idUsuario);
		cliente.getTelefones().add(telefoneDTO);
		servicoUsuario.atualizar(cliente);
		cliente = servicoUsuario.procurar(idUsuario);

		TelefoneDTO criado = ((TreeSet<TelefoneDTO>) cliente.getTelefones()).last();
		return criado;
	}

	public TelefoneDTO atualizar(@Valid TelefoneDTO telefoneDTO) {
		Long id = telefoneDTO.getId();
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		Telefone existente = repositorio.findById(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(telefoneDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(telefoneDTO));
		Telefone salvo = repositorio.save(existente);
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Usuario cliente = repositorioUsuario.findOneByTelefonesId(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		Telefone telefone = cliente.getTelefones().stream()
				.filter(t -> t.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.getTelefones().remove(telefone);
		repositorioUsuario.save(cliente);
	}
}
