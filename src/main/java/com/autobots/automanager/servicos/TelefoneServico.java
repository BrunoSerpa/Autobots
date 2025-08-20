package com.autobots.automanager.servicos;

import com.autobots.automanager.converter.TelefoneConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.validar.TelefoneValidar;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class TelefoneServico {
	private static final String NAO_ENCONTRADO = "Telefone não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String SEM_ID = "Telefone não possui ID.";
	private static final String ERRO_ENCONTRADO = "Problemas no Telefone:";

	private final ClienteServico servicoCliente;
	private final TelefoneAtualizador atualizador;
	private final TelefoneConverter conversor;
	private final TelefoneRepositorio repositorio;
	private final ClienteRepositorio repositorioCliente;
	private final TelefoneValidar validar;

	public TelefoneServico(ClienteServico servicoCliente,
			ClienteRepositorio repositorioCliente,
			TelefoneAtualizador atualizador,
			TelefoneConverter conversor,
			TelefoneRepositorio repositorio,
			TelefoneValidar validar) {
		this.servicoCliente = servicoCliente;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioCliente = repositorioCliente;
		this.validar = validar;
	}

	public TelefoneDTO procurar(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido na procura de telefone: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					log.warn("Telefone não encontrado ao procurar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<TelefoneDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public TelefoneDTO cadastro(Long idCliente, @Valid TelefoneDTO telefoneDTO) {
		if (idCliente == null || idCliente <= 0) {
			log.warn("ID de cliente inválido no cadastro de telefone: {}", idCliente);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(telefoneDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos no cadastro de telefone: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);
		cliente.getTelefones().add(telefoneDTO);
		servicoCliente.atualizar(cliente);

		TelefoneDTO criado = cliente.getTelefones()
				.get(cliente.getTelefones().size() - 1);
		log.info("Telefone cadastrado: idCliente={}, idTelefone={}",
				idCliente, criado.getId());
		return criado;
	}

	public TelefoneDTO atualizar(@Valid TelefoneDTO telefoneDTO) {
		Long id = telefoneDTO.getId();
		if (id == null || id <= 0) {
			log.warn("ID inválido ao atualizar telefone: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		Telefone existente = repositorio.findById(id)
				.orElseThrow(() -> {
					log.warn("Telefone não encontrado ao atualizar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(telefoneDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos na atualização de telefone: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(telefoneDTO));
		Telefone salvo = repositorio.save(existente);

		log.info("Telefone atualizado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido ao excluir telefone: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Cliente cliente = repositorioCliente.findOneByTelefonesId(id)
				.orElseThrow(() -> {
					log.warn("Telefone não encontrado em cliente ao excluir: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		Telefone telefone = cliente.getTelefones().stream()
				.filter(t -> t.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> {
					log.warn("Telefone não encontrado na lista do cliente ao excluir: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.getTelefones().remove(telefone);
		repositorioCliente.save(cliente);

		log.info("Telefone excluído: id={}", id);
	}
}
