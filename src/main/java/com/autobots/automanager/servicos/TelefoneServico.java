package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.TelefoneConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.validar.TelefoneValidar;

@Service
public class TelefoneServico {
	private static final String NAO_ENCONTRADO = "Telefone não encontrado";
	private static final String ERRO_ENCONTRADO = "Problemas no Telefone:";

	private ClienteServico servicoCliente;
	private ClienteRepositorio repositorioCliente;
	private TelefoneAtualizador atualizador;
	private TelefoneConverter conversor;
	private TelefoneRepositorio repositorio;
	private TelefoneValidar validar;

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
		Telefone telefone = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversor.convertToDto(telefone);
	}

	public List<TelefoneDTO> todos() {
		List<Telefone> telefones = repositorio.findAll();
		return conversor.convertToDto(telefones);
	}

	public TelefoneDTO cadastro(Long idCliente, TelefoneDTO telefoneDTO) {
		List<String> erros = validar.verificar(telefoneDTO);
		if (!erros.isEmpty()) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append(ERRO_ENCONTRADO);
			erros.forEach(erro -> mensagem.append("\n").append(erro));
			throw new IllegalArgumentException(mensagem.toString());
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);

		cliente.getTelefones().add(telefoneDTO);
		servicoCliente.atualizar(idCliente, cliente);

		return cliente.getTelefones().get(cliente.getTelefones().size() - 1);
	}

	public TelefoneDTO atualizar(Long id, TelefoneDTO telefoneDTO) {
		Telefone telefone = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		atualizador.atualizar(telefone, conversor.convertToEntity(telefoneDTO));
		repositorio.save(telefone);

		return conversor.convertToDto(telefone);
	}

	public void excluir(Long id) {
		Cliente cliente = repositorioCliente.findOneByTelefonesId(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		Telefone telefone = cliente.getTelefones().stream()
				.filter(t -> t.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		cliente.getTelefones().remove(telefone);

		repositorioCliente.save(cliente);
	}
}
